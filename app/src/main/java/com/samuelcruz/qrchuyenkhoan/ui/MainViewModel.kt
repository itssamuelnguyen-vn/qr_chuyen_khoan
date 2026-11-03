package com.samuelcruz.qrchuyenkhoan.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuelcruz.qrchuyenkhoan.data.AccountStore
import com.samuelcruz.qrchuyenkhoan.data.SavedAccount
import com.samuelcruz.qrchuyenkhoan.qr.Bank
import com.samuelcruz.qrchuyenkhoan.qr.Banks
import com.samuelcruz.qrchuyenkhoan.qr.CardData
import com.samuelcruz.qrchuyenkhoan.qr.QrCard
import com.samuelcruz.qrchuyenkhoan.qr.VietQr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** The form as typed. [amountDigits] holds digits only ("150000"); the field shows them grouped. */
data class FormState(
    val bank: Bank? = null,
    val accountNumber: String = "",
    val accountName: String = "",
    val amountDigits: String = "",
    val description: String = "",
) {
    val amount: Long? get() = amountDigits.toLongOrNull()?.takeIf { it > 0 }

    val bankError: String? get() = if (bank == null) "Chọn ngân hàng nhận tiền" else null

    val accountError: String?
        get() = when {
            accountNumber.isEmpty() -> "Nhập số tài khoản"
            !VietQr.isValidAccountNumber(accountNumber) -> "Số tài khoản không hợp lệ"
            else -> null
        }

    val nameError: String?
        get() = if (VietQr.cleanAccountName(accountName).isBlank()) "Nhập tên chủ tài khoản" else null

    val isValid: Boolean get() = bankError == null && accountError == null && nameError == null
}

/** A finished card: what was encoded and the picture of it. */
class QrResult(val card: CardData, val bitmap: Bitmap) {
    /** e.g. "QR_VCB_1234567890_150000" */
    val fileName: String
        get() = listOfNotNull("QR", card.bank.code, card.accountNumber, card.amount?.toString()).joinToString("_")
}

/** Holds the form and the last result across screen rotations (like a controller instance that survives reloads). */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    var form by mutableStateOf(FormState())
        private set

    /** Errors appear only after the first press of "Tạo mã QR", not while the user is still typing. */
    var showErrors by mutableStateOf(false)
        private set

    var result by mutableStateOf<QrResult?>(null)
        private set

    var creating by mutableStateOf(false)
        private set

    private var edited = false

    init {
        viewModelScope.launch {
            val saved = AccountStore.load(application) ?: return@launch
            if (edited) return@launch // the user started typing before the file was read
            form = form.copy(
                bank = Banks.byBin(saved.bin),
                accountNumber = saved.accountNumber,
                accountName = saved.accountName,
            )
        }
    }

    fun update(change: (FormState) -> FormState) {
        edited = true
        form = change(form)
    }

    fun create() {
        val current = form
        if (!current.isValid || creating) {
            showErrors = true
            return
        }
        val bank = current.bank ?: return
        val card = CardData(
            bank = bank,
            accountNumber = current.accountNumber,
            accountName = VietQr.cleanAccountName(current.accountName).trim(),
            amount = current.amount,
            description = VietQr.cleanDescription(current.description).ifEmpty { null },
        )
        creating = true
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.Default) { QrCard.render(getApplication(), card) }
                result = QrResult(card, bitmap)
                AccountStore.save(getApplication(), SavedAccount(bank.bin, card.accountNumber, card.accountName))
            } finally {
                creating = false
            }
        }
    }

    fun closeResult() {
        result = null
    }
}
