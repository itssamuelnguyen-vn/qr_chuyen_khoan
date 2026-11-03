package com.samuelcruz.qrchuyenkhoan

import com.samuelcruz.qrchuyenkhoan.qr.Banks
import com.samuelcruz.qrchuyenkhoan.qr.TransferRequest
import com.samuelcruz.qrchuyenkhoan.qr.VietQr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VietQrTest {
    // Reference texts decoded from codes made by img.vietqr.io on 2026-11-03.

    @Test
    fun staticCodeWithoutAmountMatchesVietQr() {
        val text = VietQr.payload(TransferRequest(bin = "970407", accountNumber = "19036781234013"))
        assertEquals(
            "00020101021138580010A000000727012800069704070114190367812340130208QRIBFTTA53037045802VN63046D09",
            text,
        )
    }

    @Test
    fun dynamicCodeWithAmountAndNoteMatchesVietQr() {
        val text = VietQr.payload(
            TransferRequest(bin = "970436", accountNumber = "1234567890", amount = 150_000, description = "Tien an trua"),
        )
        assertEquals(
            "00020101021238540010A00000072701240006970436011012345678900208QRIBFTTA530370454061500005802VN62160812Tien an trua6304026B",
            text,
        )
    }

    @Test
    fun crcMatchesTheStandardCheckValue() {
        assertEquals("29B1", VietQr.crc16("123456789"))
    }

    @Test
    fun blankNoteIsLeftOut() {
        val withBlank = VietQr.payload(TransferRequest("970436", "1234567890", description = "  !! "))
        assertEquals(VietQr.payload(TransferRequest("970436", "1234567890")), withBlank)
    }

    @Test
    fun noteLosesAccentsAndSymbolsAndIsCut() {
        assertEquals("Tien nha thang 11", VietQr.cleanDescription("Tiền nhà tháng 11!"))
        assertEquals("Dong tien dien nuoc thang", VietQr.cleanDescription("Đóng tiền điện nước tháng mười một"))
        assertTrue(VietQr.cleanDescription("x".repeat(40)).length == VietQr.MAX_DESCRIPTION_LENGTH)
    }

    @Test
    fun accountNameIsCapitalsWithoutAccents() {
        assertEquals("NGUYEN VAN AN", VietQr.cleanAccountName("Nguyễn  Văn An"))
        assertEquals("TRAN THI DIEU", VietQr.cleanAccountName("trần thị Diệu."))
    }

    @Test
    fun accountNumberRules() {
        assertTrue(VietQr.isValidAccountNumber("0071000123456"))
        assertTrue(VietQr.isValidAccountNumber("VQRQ12345"))
        assertFalse(VietQr.isValidAccountNumber(""))
        assertFalse(VietQr.isValidAccountNumber("123 456"))
        assertFalse(VietQr.isValidAccountNumber("1".repeat(20)))
    }

    @Test
    fun bankListHasUniqueSixDigitBins() {
        val bins = Banks.all.map { it.bin }
        assertEquals(bins.size, bins.toSet().size)
        assertTrue(bins.all { it.length == 6 && it.all(Char::isDigit) })
        assertNotNull(Banks.byBin("970436"))
    }
}
