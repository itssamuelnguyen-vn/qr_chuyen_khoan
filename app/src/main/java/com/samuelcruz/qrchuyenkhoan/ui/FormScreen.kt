package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.samuelcruz.qrchuyenkhoan.qr.VietQr
import com.samuelcruz.qrchuyenkhoan.qr.groupThousands

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(viewModel: MainViewModel) {
    val form = viewModel.form
    val showErrors = viewModel.showErrors
    var pickingBank by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    Scaffold(topBar = { TopAppBar(title = { Text("QR Chuyển Khoản") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                "Nhập thông tin tài khoản nhận tiền. Mã tạo ra quét được bằng mọi ứng dụng ngân hàng.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            BankField(
                text = form.bank?.let { "${it.shortName} (${it.code})" }.orEmpty(),
                error = form.bankError.takeIf { showErrors },
                onClick = { pickingBank = true },
            )

            OutlinedTextField(
                value = form.accountNumber,
                onValueChange = { text ->
                    val clean = text.uppercase().filter { it in '0'..'9' || it in 'A'..'Z' }.take(VietQr.MAX_ACCOUNT_LENGTH)
                    viewModel.update { it.copy(accountNumber = clean) }
                },
                label = { Text("Số tài khoản") },
                singleLine = true,
                isError = showErrors && form.accountError != null,
                supportingText = (form.accountError.takeIf { showErrors })?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = form.accountName,
                onValueChange = { text -> viewModel.update { it.copy(accountName = text.take(50)) } },
                label = { Text("Tên chủ tài khoản") },
                singleLine = true,
                isError = showErrors && form.nameError != null,
                supportingText = {
                    Text(form.nameError.takeIf { showErrors } ?: "Sẽ in hoa, không dấu, như trên thẻ ngân hàng")
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            AmountField(
                digits = form.amountDigits,
                onDigitsChange = { digits -> viewModel.update { it.copy(amountDigits = digits) } },
            )

            OutlinedTextField(
                value = form.description,
                onValueChange = { text -> viewModel.update { it.copy(description = text.take(60)) } },
                label = { Text("Nội dung chuyển khoản (không bắt buộc)") },
                singleLine = true,
                supportingText = {
                    val cleaned = VietQr.cleanDescription(form.description)
                    Text("${cleaned.length}/${VietQr.MAX_DESCRIPTION_LENGTH} ký tự, sẽ bỏ dấu")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focus.clearFocus()
                    viewModel.create()
                }),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    focus.clearFocus()
                    viewModel.create()
                },
                enabled = !viewModel.creating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                if (viewModel.creating) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Tạo mã QR", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    if (pickingBank) {
        BankPickerSheet(
            selected = form.bank,
            onPick = { bank ->
                viewModel.update { it.copy(bank = bank) }
                pickingBank = false
            },
            onDismiss = { pickingBank = false },
        )
    }
}

/** Looks like a text field but opens the bank list. */
@Composable
private fun BankField(text: String, error: String?, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            label = { Text("Ngân hàng") },
            placeholder = { Text("Chọn ngân hàng") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            singleLine = true,
            enabled = false, // taps go to the Box below, which opens the list
            isError = error != null,
            supportingText = error?.let { { Text(it, color = colors.error) } },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = colors.onSurface,
                disabledBorderColor = if (error != null) colors.error else colors.outline,
                disabledLabelColor = if (error != null) colors.error else colors.onSurfaceVariant,
                disabledPlaceholderColor = colors.onSurfaceVariant,
                disabledTrailingIconColor = colors.onSurfaceVariant,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(bottom = if (error != null) 22.dp else 0.dp)
                .clickable(onClick = onClick),
        )
    }
}

/** Digits only, shown as "1.500.000". The cursor stays at the end, which is how people type amounts anyway. */
@Composable
private fun AmountField(digits: String, onDigitsChange: (String) -> Unit) {
    val shown = digits.toLongOrNull()?.let(::groupThousands).orEmpty()
    var field by remember { mutableStateOf(TextFieldValue(shown, TextRange(shown.length))) }
    if (field.text != shown) field = TextFieldValue(shown, TextRange(shown.length))

    OutlinedTextField(
        value = field,
        onValueChange = { value ->
            val newDigits = value.text.filter(Char::isDigit).trimStart('0').take(13)
            val newShown = newDigits.toLongOrNull()?.let(::groupThousands).orEmpty()
            field = if (newShown == value.text) value else TextFieldValue(newShown, TextRange(newShown.length))
            if (newDigits != digits) onDigitsChange(newDigits)
        },
        label = { Text("Số tiền (không bắt buộc)") },
        suffix = { Text("đ") },
        singleLine = true,
        supportingText = { Text("Để trống thì người chuyển tự nhập số tiền") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
    )
}
