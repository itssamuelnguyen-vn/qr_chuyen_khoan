package com.samuelcruz.qrchuyenkhoan.qr

/**
 * How a bank is drawn without shipping its logo: a coloured badge with a few letters. Colours are close to each
 * bank's own; banks not listed get a colour picked from [fallback] by their BIN, so it never changes.
 */
object BankStyle {
    private val brand: Map<String, Int> = mapOf(
        "970436" to 0xFF00703C.toInt(), // Vietcombank green
        "970415" to 0xFF005993.toInt(), // VietinBank blue
        "970418" to 0xFF006B68.toInt(), // BIDV teal
        "970405" to 0xFFA6192E.toInt(), // Agribank maroon
        "970422" to 0xFF1434CB.toInt(), // MB blue
        "970407" to 0xFFE2231A.toInt(), // Techcombank red
        "970416" to 0xFF1F4E9D.toInt(), // ACB blue
        "970432" to 0xFF00A651.toInt(), // VPBank green
        "970423" to 0xFF5E2A84.toInt(), // TPBank purple
        "970403" to 0xFF0A4A9C.toInt(), // Sacombank blue
        "970437" to 0xFFD71920.toInt(), // HDBank red
        "970441" to 0xFF0066B3.toInt(), // VIB blue
        "970443" to 0xFFE8731B.toInt(), // SHB orange
        "970431" to 0xFF0A4DA2.toInt(), // Eximbank blue
        "970426" to 0xFFE35205.toInt(), // MSB orange
        "970448" to 0xFF00843D.toInt(), // OCB green
        "970440" to 0xFFD2232A.toInt(), // SeABank red
        "970449" to 0xFFF26F21.toInt(), // LPBank orange
        "970454" to 0xFFE76E1F.toInt(), // VietCapitalBank orange
        "970412" to 0xFFD71A21.toInt(), // PVcomBank red
        "970425" to 0xFF00958F.toInt(), // ABBANK teal
        "970428" to 0xFF00529C.toInt(), // NamABank blue
        "970452" to 0xFF0B4EA2.toInt(), // KienLongBank blue
        "546034" to 0xFFE6007E.toInt(), // CAKE pink
        "963388" to 0xFF5B2C83.toInt(), // Timo purple
        "971025" to 0xFFA50064.toInt(), // MoMo magenta
        "971101" to 0xFF0068FF.toInt(), // ZaloPay blue
        "971005" to 0xFFEE0033.toInt(), // Viettel Money red
    )

    private val fallback = intArrayOf(
        0xFF0F766E.toInt(), 0xFF1D4ED8.toInt(), 0xFF7C3AED.toInt(), 0xFFBE185D.toInt(),
        0xFFB45309.toInt(), 0xFF047857.toInt(), 0xFF0369A1.toInt(), 0xFF9F1239.toInt(),
    )

    /** The most used banks, shown first in the picker. */
    val popular: List<String> = listOf("970436", "970422", "970407", "970415", "970418", "970405", "970416", "970432")

    fun color(bank: Bank): Int = brand[bank.bin] ?: fallback[(bank.bin.toIntOrNull() ?: 0) % fallback.size]

    /** 2-4 letters for the badge: the bank's code when short ("VCB", "MB"), else the start of its short name. */
    fun monogram(bank: Bank): String {
        val code = bank.code.filter(Char::isLetterOrDigit).uppercase()
        return if (code.length in 2..4) code else bank.shortName.filter(Char::isLetterOrDigit).take(3).uppercase()
    }
}
