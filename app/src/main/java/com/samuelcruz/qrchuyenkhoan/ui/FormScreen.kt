package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuelcruz.qrchuyenkhoan.R
import com.samuelcruz.qrchuyenkhoan.qr.Bank
import com.samuelcruz.qrchuyenkhoan.qr.VietQr
import com.samuelcruz.qrchuyenkhoan.qr.groupThousands

private val fieldShape = RoundedCornerShape(14.dp)
private val quickAmounts = listOf(50_000L, 100_000L, 200_000L, 500_000L, 1_000_000L, 2_000_000L)

@Composable
fun FormScreen(viewModel: MainViewModel) {
    val form = viewModel.form
    val showErrors = viewModel.showErrors
    var pickingBank by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val submit = {
        focus.clearFocus()
        viewModel.create()
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Header()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .offset(y = (-36).dp)
                    .padding(horizontal = 16.dp),
            ) {
                SectionCard("Tài khoản nhận tiền") {
                    BankSelector(
                        bank = form.bank,
                        error = form.bankError.takeIf { showErrors },
                        onClick = { pickingBank = true },
                    )
                    OutlinedTextField(
                        value = form.accountNumber,
                        onValueChange = { text ->
                            val clean = text.uppercase().filter { it in '0'..'9' || it in 'A'..'Z' }
                                .take(VietQr.MAX_ACCOUNT_LENGTH)
                            viewModel.update { it.copy(accountNumber = clean) }
                        },
                        label = { Text("Số tài khoản") },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_bank), contentDescription = null) },
                        singleLine = true,
                        shape = fieldShape,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 1.sp),
                        isError = showErrors && form.accountError != null,
                        supportingText = form.accountError.takeIf { showErrors }?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = form.accountName,
                        onValueChange = { text -> viewModel.update { it.copy(accountName = text.take(50)) } },
                        label = { Text("Tên chủ tài khoản") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        singleLine = true,
                        shape = fieldShape,
                        isError = showErrors && form.nameError != null,
                        supportingText = form.nameError.takeIf { showErrors }?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                SectionCard("Số tiền & nội dung") {
                    AmountField(
                        digits = form.amountDigits,
                        onDigitsChange = { digits -> viewModel.update { it.copy(amountDigits = digits) } },
                    )
                    QuickAmounts(
                        selected = form.amount,
                        onPick = { amount ->
                            val digits = if (form.amount == amount) "" else amount.toString()
                            viewModel.update { it.copy(amountDigits = digits) }
                        },
                    )
                    val cleanedNote = VietQr.cleanDescription(form.description)
                    OutlinedTextField(
                        value = form.description,
                        onValueChange = { text -> viewModel.update { it.copy(description = text.take(60)) } },
                        label = { Text("Nội dung") },
                        placeholder = { Text("Ví dụ: Tien nha thang 11") },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        singleLine = true,
                        shape = fieldShape,
                        supportingText = {
                            Row {
                                Text("Không bắt buộc, tự bỏ dấu", modifier = Modifier.weight(1f))
                                Text("${cleanedNote.length}/${VietQr.MAX_DESCRIPTION_LENGTH}")
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                GradientButton(
                    text = "Tạo mã QR",
                    icon = painterResource(R.drawable.ic_qr),
                    onClick = submit,
                    enabled = !viewModel.creating,
                    loading = viewModel.creating,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Mã theo chuẩn VietQR (NAPAS 247): quét được bằng mọi ứng dụng ngân hàng và ví điện tử. " +
                        "Tạo ngay trên máy, không cần mạng.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .navigationBarsPadding(),
                )
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

/** Teal gradient band with the title and a large faded QR mark. */
@Composable
private fun Header() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(BrandGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
    ) {
        Icon(
            painterResource(R.drawable.ic_qr),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 28.dp, y = 6.dp)
                .size(170.dp)
                .rotate(-12f)
                .alpha(0.12f),
        )
        Column(
            Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 64.dp),
        ) {
            Text(
                "QR Chuyển Khoản",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Tạo mã nhận tiền cho mọi ngân hàng chỉ trong vài giây",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/** Shows the chosen bank with its badge, or an invitation to choose one; opens the bank list. */
@Composable
private fun BankSelector(bank: Bank?, error: String?, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val borderColor = when {
        error != null -> colors.error
        bank != null -> bankColor(bank).copy(alpha = 0.55f)
        else -> colors.outline
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(if (bank != null) 1.5.dp else 1.dp, borderColor), fieldShape)
                .background(
                    if (bank != null) bankColor(bank).copy(alpha = 0.07f) else Color.Transparent,
                    fieldShape,
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            if (bank != null) {
                BankBadge(bank, size = 44.dp)
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(colors.primaryContainer, RoundedCornerShape(12.dp)),
                ) {
                    Icon(painterResource(R.drawable.ic_bank), contentDescription = null, tint = colors.primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                if (bank != null) {
                    Text(bank.shortName, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                    Text(
                        bank.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text("Chọn ngân hàng", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                    Text("Hơn 70 ngân hàng và ví điện tử", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = colors.onSurfaceVariant)
        }
        if (error != null) {
            Text(
                error,
                color = colors.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
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
        label = { Text("Số tiền") },
        placeholder = { Text("Để trống: người chuyển tự nhập") },
        leadingIcon = {
            Text(
                "₫",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        shape = fieldShape,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
    )
}

/** One-tap amounts; tapping the selected one clears it. */
@Composable
private fun QuickAmounts(selected: Long?, onPick: (Long) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        quickAmounts.forEach { amount ->
            FilterChip(
                selected = selected == amount,
                onClick = { onPick(amount) },
                label = { Text(shortAmount(amount)) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}

/** 50000 → "50K", 1000000 → "1 triệu". */
private fun shortAmount(amount: Long): String =
    if (amount >= 1_000_000) "${amount / 1_000_000} triệu" else "${amount / 1_000}K"
