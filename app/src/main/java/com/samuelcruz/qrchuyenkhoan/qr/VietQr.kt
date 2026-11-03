package com.samuelcruz.qrchuyenkhoan.qr

import java.text.Normalizer

/** What goes into one transfer QR code. */
data class TransferRequest(
    val bin: String,
    val accountNumber: String,
    /** Đồng; null leaves the amount for the payer to type. */
    val amount: Long? = null,
    /** Transfer note; null or blank means none. */
    val description: String? = null,
)

/**
 * Builds the text of a VietQR code: the NAPAS profile of the EMVCo merchant-presented QR standard, which every
 * Vietnamese banking app can scan. It is a chain of TLV fields (2-digit id, 2-digit length, value) ending with a
 * CRC-16 checksum, so it can be made offline with no API key.
 *
 * The account holder's name is not part of the code: the payer's bank looks it up from the account number.
 */
object VietQr {
    const val MAX_ACCOUNT_LENGTH = 19
    const val MAX_DESCRIPTION_LENGTH = 25
    const val MAX_AMOUNT = 9_999_999_999_999L // 13 digits, the longest the amount field allows

    private const val NAPAS_GUID = "A000000727"
    private const val SERVICE_TO_ACCOUNT = "QRIBFTTA"
    private const val CURRENCY_VND = "704"

    fun payload(request: TransferRequest): String {
        require(request.bin.length == 6 && request.bin.all(Char::isDigit)) { "BIN must be 6 digits" }
        require(isValidAccountNumber(request.accountNumber)) { "Invalid account number" }
        require(request.amount == null || request.amount in 1..MAX_AMOUNT) { "Amount out of range" }
        val description = request.description?.let(::cleanDescription)?.takeIf { it.isNotEmpty() }

        val beneficiary = field("00", request.bin) + field("01", request.accountNumber)
        val merchantAccount = field("00", NAPAS_GUID) + field("01", beneficiary) + field("02", SERVICE_TO_ACCOUNT)
        val body = buildString {
            append(field("00", "01")) // payload format version
            // 11 = static code (reusable), 12 = dynamic code (one payment with a fixed amount).
            append(field("01", if (request.amount != null) "12" else "11"))
            append(field("38", merchantAccount))
            append(field("53", CURRENCY_VND))
            request.amount?.let { append(field("54", it.toString())) }
            append(field("58", "VN"))
            description?.let { append(field("62", field("08", it))) }
            append("6304") // the CRC field's id and length are part of the checksummed text
        }
        return body + crc16(body)
    }

    fun isValidAccountNumber(text: String): Boolean =
        text.length in 1..MAX_ACCOUNT_LENGTH && text.all { it in '0'..'9' || it in 'A'..'Z' }

    /**
     * Banks show transfer notes without accents and some reject symbols, so the note is reduced to A-Z, a-z, 0-9 and
     * single spaces ("Tiền nhà tháng 11!" → "Tien nha thang 11"), then cut to [MAX_DESCRIPTION_LENGTH].
     */
    fun cleanDescription(text: String): String =
        removeAccents(text)
            .replace(Regex("[^A-Za-z0-9 ]"), " ")
            .replace(Regex(" +"), " ")
            .trim()
            .take(MAX_DESCRIPTION_LENGTH)
            .trimEnd()

    /** Account names are written in capitals without accents, as banks print them ("Nguyễn Văn An" → "NGUYEN VAN AN"). */
    fun cleanAccountName(text: String): String =
        removeAccents(text).uppercase().replace(Regex("[^A-Z0-9 ]"), "").replace(Regex(" +"), " ").trimStart()

    fun removeAccents(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace('đ', 'd')
            .replace('Đ', 'D')

    private fun field(id: String, value: String): String {
        require(value.length <= 99) { "Field $id too long" }
        return id + value.length.toString().padStart(2, '0') + value
    }

    /** CRC-16/CCITT-FALSE (polynomial 0x1021, start 0xFFFF), as 4 upper-case hex digits. */
    internal fun crc16(text: String): String {
        var crc = 0xFFFF
        for (byte in text.toByteArray(Charsets.UTF_8)) {
            crc = crc xor ((byte.toInt() and 0xFF) shl 8)
            repeat(8) {
                crc = if (crc and 0x8000 != 0) (crc shl 1) xor 0x1021 else crc shl 1
            }
            crc = crc and 0xFFFF
        }
        return crc.toString(16).uppercase().padStart(4, '0')
    }
}
