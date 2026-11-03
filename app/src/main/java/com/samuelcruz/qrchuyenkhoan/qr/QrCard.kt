package com.samuelcruz.qrchuyenkhoan.qr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
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
 * Draws the card into one bitmap: a header in the bank's colour with its badge, the QR code inside a scan frame,
 * then name, account, amount and note. The app shows this same bitmap on screen, saves it and shares it, so what
 * you see is exactly what gets sent. The QR itself is always black on white so every scanner reads it.
 */
object QrCard {
    private const val WIDTH = 1080
    private const val PAD = 72
    private const val QR_SIZE = 700
    private const val INK = 0xFF111827.toInt()
    private const val MUTED = 0xFF6B7280.toInt()
    private const val LINE = 0xFFE5E7EB.toInt()

    fun render(context: Context, data: CardData): Bitmap {
        val fonts = Fonts(context)
        val accent = BankStyle.color(data.bank)
        val textWidth = WIDTH - 2 * PAD

        // Header: badge on the left, bank names on the right.
        val badgeSize = 132
        val headerTextWidth = textWidth - badgeSize - 36
        val bankLayout = layout(data.bank.shortName, fonts.bold, 58f, Color.WHITE, headerTextWidth, Layout.Alignment.ALIGN_NORMAL)
        val bankNameLayout = layout(data.bank.name, fonts.regular, 30f, 0xE6FFFFFF.toInt(), headerTextWidth, Layout.Alignment.ALIGN_NORMAL)
        val headerTextHeight = bankLayout.height + 6 + bankNameLayout.height
        val bandHeight = 64 + maxOf(badgeSize, headerTextHeight) + 64

        val nameLayout = layout(data.accountName, fonts.bold, 52f, INK, textWidth)
        val accountLayout = layout(data.accountNumber, fonts.semibold, 46f, MUTED, textWidth, letterSpacing = 0.08f)
        val amountLayout = data.amount?.let { layout(formatVnd(it), fonts.bold, 60f, accent, textWidth) }
        val noteLayout = data.description?.takeIf { it.isNotBlank() }
            ?.let { layout("Nội dung: $it", fonts.medium, 36f, INK, textWidth) }
        val brandLayout = layout("VietQR · NAPAS 247", fonts.bold, 30f, accent, textWidth, letterSpacing = 0.04f)
        val footerLayout = layout("Quét mã bằng ứng dụng ngân hàng hoặc ví điện tử", fonts.regular, 28f, MUTED, textWidth)

        val modules = qrModules(data.payload)
        val moduleSize = QR_SIZE / modules.size // whole pixels keep the code sharp
        val qrSize = moduleSize * modules.size

        val qrTop = bandHeight + 110
        var height = qrTop + qrSize + 110 + nameLayout.height + 10 + accountLayout.height
        amountLayout?.let { height += 40 + it.height + 2 * 22 }
        noteLayout?.let { height += 28 + it.height }
        height += 56 + 2 + 40 + brandLayout.height + 6 + footerLayout.height + 60

        val bitmap = createBitmap(WIDTH, height)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Header band with a soft diagonal gradient.
        val bandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, WIDTH.toFloat(), bandHeight.toFloat(),
                accent, ColorUtils.blendARGB(accent, Color.BLACK, 0.28f), Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), bandHeight.toFloat(), bandPaint)
        // Decorative circles for depth.
        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0x14FFFFFF }
        canvas.drawCircle(WIDTH - 90f, 40f, 210f, glow)
        canvas.drawCircle(WIDTH - 260f, bandHeight + 30f, 130f, glow)

        val badgeTop = (bandHeight - badgeSize) / 2f
        drawBadge(canvas, data.bank, accent, fonts.bold, PAD.toFloat(), badgeTop, badgeSize.toFloat())
        val headerTextLeft = PAD + badgeSize + 36
        var y = (bandHeight - headerTextHeight) / 2
        y = draw(canvas, bankLayout, headerTextLeft, y) + 6
        draw(canvas, bankNameLayout, headerTextLeft, y)

        // QR code with corner brackets around it.
        val qrLeft = (WIDTH - qrSize) / 2
        drawScanFrame(canvas, accent, qrLeft.toFloat(), qrTop.toFloat(), qrSize.toFloat())
        val dark = Paint().apply { color = Color.BLACK }
        for (row in modules.indices) {
            for (col in modules.indices) {
                if (!modules[row][col]) continue
                val left = (qrLeft + col * moduleSize).toFloat()
                val top = (qrTop + row * moduleSize).toFloat()
                canvas.drawRect(left, top, left + moduleSize, top + moduleSize, dark)
            }
        }

        y = qrTop + qrSize + 110
        y = draw(canvas, nameLayout, PAD, y) + 10
        y = draw(canvas, accountLayout, PAD, y)
        amountLayout?.let { layout ->
            y += 40
            val pillWidth = layout.getLineWidth(0) + 2 * 48
            val pill = RectF((WIDTH - pillWidth) / 2, y.toFloat(), (WIDTH + pillWidth) / 2, (y + layout.height + 44).toFloat())
            canvas.drawRoundRect(pill, pill.height() / 2, pill.height() / 2, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ColorUtils.setAlphaComponent(accent, 0x1F)
            })
            y = draw(canvas, layout, PAD, y + 22) + 22
        }
        noteLayout?.let { y = draw(canvas, it, PAD, y + 28) }

        y += 56
        val dash = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = LINE
            strokeWidth = 3f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(18f, 12f), 0f)
        }
        canvas.drawLine(PAD.toFloat(), y.toFloat(), (WIDTH - PAD).toFloat(), y.toFloat(), dash)
        y += 2 + 40
        y = draw(canvas, brandLayout, PAD, y) + 6
        draw(canvas, footerLayout, PAD, y)
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

    /** White rounded square with the bank's letters in its colour. */
    private fun drawBadge(canvas: Canvas, bank: Bank, accent: Int, typeface: Typeface, left: Float, top: Float, size: Float) {
        val rect = RectF(left, top, left + size, top + size)
        canvas.drawRoundRect(rect, size * 0.28f, size * 0.28f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
        val letters = BankStyle.monogram(bank)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            color = accent
            textAlign = Paint.Align.CENTER
            textSize = size * when (letters.length) {
                1, 2 -> 0.38f
                3 -> 0.31f
                else -> 0.25f
            }
        }
        val baseline = rect.centerY() - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(letters, rect.centerX(), baseline, paint)
    }

    /** Four rounded L-shaped corners, like a camera viewfinder, a little outside the code. */
    private fun drawScanFrame(canvas: Canvas, accent: Int, left: Float, top: Float, size: Float) {
        val gap = 34f
        val arm = 96f
        val radius = 30f
        val l = left - gap
        val t = top - gap
        val r = left + size + gap
        val b = top + size + gap
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accent
            style = Paint.Style.STROKE
            strokeWidth = 12f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        fun corner(x: Float, y: Float, dx: Float, dy: Float) {
            val path = Path().apply {
                moveTo(x, y + dy * arm)
                lineTo(x, y + dy * radius)
                quadTo(x, y, x + dx * radius, y)
                lineTo(x + dx * arm, y)
            }
            canvas.drawPath(path, paint)
        }
        corner(l, t, 1f, 1f)
        corner(r, t, -1f, 1f)
        corner(l, b, 1f, -1f)
        corner(r, b, -1f, -1f)
    }

    private class Fonts(context: Context) {
        val regular = font(context, R.font.be_vietnam_pro_regular)
        val medium = font(context, R.font.be_vietnam_pro_medium)
        val semibold = font(context, R.font.be_vietnam_pro_semibold)
        val bold = font(context, R.font.be_vietnam_pro_bold)

        private fun font(context: Context, @FontRes id: Int): Typeface =
            ResourcesCompat.getFont(context, id) ?: Typeface.DEFAULT
    }

    private fun layout(
        text: String,
        typeface: Typeface,
        size: Float,
        color: Int,
        width: Int,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER,
        letterSpacing: Float = 0f,
    ): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = size
            this.color = color
            this.letterSpacing = letterSpacing
        }
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .build()
    }

    /** Draws [layout] with its top-left at ([left], [top]) and returns the y just below it. */
    private fun draw(canvas: Canvas, layout: StaticLayout, left: Int, top: Int): Int {
        canvas.save()
        canvas.translate(left.toFloat(), top.toFloat())
        layout.draw(canvas)
        canvas.restore()
        return top + layout.height
    }
}
