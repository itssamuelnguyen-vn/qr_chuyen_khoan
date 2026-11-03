package com.samuelcruz.qrchuyenkhoan

import com.samuelcruz.qrchuyenkhoan.qr.Banks
import com.samuelcruz.qrchuyenkhoan.qr.formatVnd
import com.samuelcruz.qrchuyenkhoan.qr.groupThousands
import com.samuelcruz.qrchuyenkhoan.ui.FormState
import com.samuelcruz.qrchuyenkhoan.ui.filterBanks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatAndSearchTest {
    @Test
    fun amountsAreGroupedWithDots() {
        assertEquals("0", groupThousands(0))
        assertEquals("999", groupThousands(999))
        assertEquals("1.000", groupThousands(1000))
        assertEquals("1.500.000", groupThousands(1_500_000))
        assertEquals("150.000 đ", formatVnd(150_000))
    }

    @Test
    fun bankSearchIgnoresAccentsAndCase() {
        assertEquals("970436", filterBanks("vcb").first().bin)
        assertEquals("970436", filterBanks("Ngoại thương").first().bin)
        assertEquals("970422", filterBanks("quan doi").first().bin)
        assertEquals("970407", filterBanks("  TECHCOM ").single().bin)
        assertEquals(Banks.all, filterBanks(""))
        assertTrue(filterBanks("khong co ngan hang nay").isEmpty())
    }

    @Test
    fun formNeedsBankAccountAndName() {
        val empty = FormState()
        assertFalse(empty.isValid)
        val filled = FormState(Banks.byBin("970436"), "1234567890", "Nguyễn Văn An")
        assertTrue(filled.isValid)
        assertNull(filled.amount)
        assertEquals(150_000L, filled.copy(amountDigits = "150000").amount)
        assertFalse(filled.copy(accountName = " .. ").isValid)
    }
}

class BankStyleTest {
    @Test
    fun monogramsAreShort() {
        val vcb = com.samuelcruz.qrchuyenkhoan.qr.Banks.byBin("970436")!!
        assertEquals("VCB", com.samuelcruz.qrchuyenkhoan.qr.BankStyle.monogram(vcb))
        com.samuelcruz.qrchuyenkhoan.qr.Banks.all.forEach {
            assertTrue(it.shortName, com.samuelcruz.qrchuyenkhoan.qr.BankStyle.monogram(it).length in 2..4)
        }
    }

    @Test
    fun popularBanksExist() {
        com.samuelcruz.qrchuyenkhoan.qr.BankStyle.popular.forEach { assertTrue(it, Banks.byBin(it) != null) }
    }
}
