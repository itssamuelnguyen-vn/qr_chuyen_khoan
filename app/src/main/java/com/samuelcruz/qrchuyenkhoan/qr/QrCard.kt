package com.samuelcruz.qrchuyenkhoan.qr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.samuelcruz.qrchuyenkhoan.R

/** Everything printed on the shareable card. */
data class CardData(
    val bank: Bank,
    val accountNumber: String,
    val accountName: String,
    val amount: Long?,
    val description: String?,
) {
    val payload: String
        get() = VietQr.payload(TransferRequest(bank.bin, accountNumber, amount, description))
}

/**
 * Draws the card (bank header, QR code, name, account, amount, note) into one bitmap. The app shows this same bitmap
 * on screen, saves it and shares it, so what you see is exactly what gets sent.
 */
object QrCard {
    const val BRAND = 0xFF0F766E.toInt()
    private const val WIDTH = 1080
    private const val PAD = 72
    private const val QR_SIZE = 760
    private const val INK = 0xFF1F2937.toInt()
    private const val MUTED = 0xFF6B7280.toInt()

    fun render(context: Context, data: CardData): Bitmap {
        val bold = font(context, R.font.be_vietnam_pro_bold)
        val semibold = font(context, R.font.be_vietnam_pro_semibold)
        val regular = font(context, R.font.be_vietnam_pro_regular)
        val textWidth = WIDTH - 2 * PAD

        val bankLayout = layout(data.bank.shortName, bold, 64f, Color.WHITE, textWidth)
        val bankNameLayout = layout(data.bank.name, regular, 32f, 0xD9FFFFFF.toInt(), textWidth)
        val nameLayout = layout(data.accountName, bold, 52f, INK, textWidth)
        val accountLayout = layout(data.accountNumber, semibold, 46f, INK, textWidth, letterSpacing = 0.06f)
        val amountLayout = data.amount?.let { layout(formatVnd(it), bold, 62f, BRAND, textWidth) }
        val noteLayout = data.description?.takeIf { it.isNotBlank() }
            ?.let { layout("Nội dung: $it", regular, 38f, MUTED, textWidth) }
        val footerLayout = layout("Quét mã bằng ứng dụng ngân hàng hoặc ví điện tử", regular, 30f, MUTED, textWidth)

        val modules = qrModules(data.payload)
        val moduleSize = QR_SIZE / modules.size // whole pixels keep the code sharp
        val qrSize = moduleSize * modules.size

        val bandHeight = 64 + bankLayout.height + 12 + bankNameLayout.height + 56
        val qrTop = bandHeight + 72
        var height = qrTop + qrSize + 56 + nameLayout.height + 12 + accountLayout.height
        amountLayout?.let { height += 28 + it.height }
        noteLayout?.let { height += 16 + it.height }
        height += 56 + footerLayout.height + 64

        val bitmap = createBitmap(WIDTH, height)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), bandHeight.toFloat(), Paint().apply { color = BRAND })

        var y = 64
        y = draw(canvas, bankLayout, y) + 12
        draw(canvas, bankNameLayout, y)

        val qrLeft = (WIDTH - qrSize) / 2
        val dark = Paint().apply { color = Color.BLACK }
        for (row in modules.indices) {
            for (col in modules.indices) {
                if (!modules[row][col]) continue
                val left = (qrLeft + col * moduleSize).toFloat()
                val top = (qrTop + row * moduleSize).toFloat()
                canvas.drawRect(left, top, left + moduleSize, top + moduleSize, dark)
            }
        }

        y = qrTop + qrSize + 56
        y = draw(canvas, nameLayout, y) + 12
        y = draw(canvas, accountLayout, y)
        amountLayout?.let { y = draw(canvas, it, y + 28) }
        noteLayout?.let { y = draw(canvas, it, y + 16) }
        draw(canvas, footerLayout, y + 56)
        return bitmap
    }

    /** The QR code as rows of dark (true) / light modules, without the quiet zone (the card's white is the margin). */
    fun qrModules(text: String): List<BooleanArray> {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 0,
        )
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 0, 0, hints)
        return List(matrix.height) { y -> BooleanArray(matrix.width) { x -> matrix[x, y] } }
    }

    private fun font(context: Context, @FontRes id: Int): Typeface =
        ResourcesCompat.getFont(context, id) ?: Typeface.DEFAULT

    private fun layout(
        text: String,
        typeface: Typeface,
        size: Float,
        color: Int,
        width: Int,
        letterSpacing: Float = 0f,
    ): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = size
            this.color = color
            this.letterSpacing = letterSpacing
        }
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
    }

    /** Draws [layout] at [top] (left padding fixed) and returns the y just below it. */
    private fun draw(canvas: Canvas, layout: StaticLayout, top: Int): Int {
        canvas.save()
        canvas.translate(PAD.toFloat(), top.toFloat())
        layout.draw(canvas)
        canvas.restore()
        return top + layout.height
    }
}
