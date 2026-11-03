package com.samuelcruz.qrchuyenkhoan.ui

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samuelcruz.qrchuyenkhoan.data.ImageExport
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(result: QrResult, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val image = remember(result) { result.bitmap.asImageBitmap() }
    FullBrightness()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mã QR chuyển khoản") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    bitmap = image,
                    contentDescription = "Mã QR chuyển khoản đến ${result.card.accountName}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val saved = ImageExport.saveToGallery(context, result.bitmap, result.fileName)
                            snackbar.showSnackbar(
                                if (saved) "Đã lưu vào Ảnh › ${ImageExport.ALBUM}" else "Không lưu được ảnh",
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                ) { Text("Lưu ảnh") }
                Button(
                    onClick = { scope.launch { ImageExport.share(context, result.bitmap, result.fileName) } },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Chia sẻ")
                }
            }
            TextButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp)) { Text("Tạo mã khác") }
        }
    }
}

/** Turns the screen to full brightness while the code is shown, so it scans well; restores it on leaving. */
@Composable
private fun FullBrightness() {
    val window = LocalActivity.current?.window ?: return
    DisposableEffect(window) {
        val before = window.attributes.screenBrightness
        window.attributes = window.attributes.apply { screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL }
        onDispose {
            window.attributes = window.attributes.apply { screenBrightness = before }
        }
    }
}
