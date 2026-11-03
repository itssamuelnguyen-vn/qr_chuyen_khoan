package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuelcruz.qrchuyenkhoan.qr.Bank
import com.samuelcruz.qrchuyenkhoan.qr.BankStyle
import com.samuelcruz.qrchuyenkhoan.qr.Banks
import com.samuelcruz.qrchuyenkhoan.qr.VietQr

/** Accent-free, lower-case text a bank is found by: "vcb vietcombank ngan hang tmcp ngoai thuong viet nam 970436". */
private fun searchText(bank: Bank): String =
    VietQr.removeAccents("${bank.code} ${bank.shortName} ${bank.name} ${bank.bin}").lowercase()

/** Every word typed must appear somewhere ("ngoai thuong", "vcb" and "vietcom" all find Vietcombank). */
fun filterBanks(query: String): List<Bank> {
    val words = VietQr.removeAccents(query).lowercase().split(' ').filter { it.isNotBlank() }
    if (words.isEmpty()) return Banks.all
    return Banks.all.filter { bank -> searchText(bank).let { text -> words.all { it in text } } }
}

private val popularBanks: List<Bank> = BankStyle.popular.mapNotNull(Banks::byBin)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPickerSheet(selected: Bank?, onPick: (Bank) -> Unit, onDismiss: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val banks = remember(query) { filterBanks(query) }
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surfaceContainer,
    ) {
        Column(Modifier.fillMaxHeight(0.94f)) {
            Text(
                "Chọn ngân hàng",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            )
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Tìm tên hoặc mã ngân hàng", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Clear, contentDescription = "Xoá") }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceContainerHighest,
                    unfocusedContainerColor = colors.surfaceContainerHighest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.navigationBarsPadding()) {
                if (query.isBlank()) {
                    item(key = "popular") {
                        SectionLabel("Phổ biến")
                        PopularGrid(selected, onPick)
                        SectionLabel("Tất cả ngân hàng")
                    }
                }
                if (banks.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            "Không tìm thấy ngân hàng nào",
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        )
                    }
                }
                items(banks, key = { it.bin }) { bank ->
                    BankRow(bank, isSelected = bank.bin == selected?.bin, onClick = { onPick(bank) })
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
    )
}

/** The eight most used banks as big tiles, four per row. */
@Composable
private fun PopularGrid(selected: Bank?, onPick: (Bank) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        popularBanks.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { bank ->
                    val isSelected = bank.bin == selected?.bin
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) bankColor(bank).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainerHighest,
                                RoundedCornerShape(16.dp),
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) bankColor(bank) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .clickable { onPick(bank) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                    ) {
                        BankBadge(bank, size = 40.dp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            bank.shortName,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BankRow(bank: Bank, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) bankColor(bank).copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        BankBadge(bank, size = 42.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(bank.shortName, style = MaterialTheme.typography.titleMedium)
            Text(
                bank.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (isSelected) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Đang chọn", tint = bankColor(bank))
        }
    }
}
