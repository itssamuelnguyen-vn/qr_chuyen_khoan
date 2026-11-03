package com.samuelcruz.qrchuyenkhoan.data

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Saving the card to the gallery and handing it to other apps (Zalo, Messenger, ...). */
object ImageExport {
    const val ALBUM = "QR Chuyển Khoản"

    /**
     * Saves into Pictures/QR Chuyển Khoản through MediaStore, which needs no storage permission on Android 10+.
     * Returns false when the gallery refused the file.
     */
    suspend fun saveToGallery(context: Context, bitmap: Bitmap, fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext false
            val written = runCatching {
                resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } == true
            }.getOrDefault(false)
            if (!written) {
                resolver.delete(uri, null, null)
                return@withContext false
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            true
        }

    /** Writes the card to the app's cache and opens the system share sheet. */
    suspend fun share(context: Context, bitmap: Bitmap, fileName: String) {
        val file = withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "shared").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() } // only the latest card is kept
            File(dir, "$fileName.png").also { out ->
                out.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri(null, uri) // lets the share sheet show a preview
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(send, "Chia sẻ mã QR"))
    }
}
