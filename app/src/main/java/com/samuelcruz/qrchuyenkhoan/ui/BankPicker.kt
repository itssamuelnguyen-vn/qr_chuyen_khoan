package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.samuelcruz.qrchuyenkhoan.qr.Bank
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPickerSheet(selected: Bank?, onPick: (Bank) -> Unit, onDismiss: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val banks = remember(query) { filterBanks(query) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(Modifier.fillMaxHeight(0.92f)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Tìm: VCB, Techcombank, quân đội...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Clear, contentDescription = "Xoá") }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
            if (banks.isEmpty()) {
                Text(
                    "Không tìm thấy ngân hàng nào",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            }
            LazyColumn {
                items(banks, key = { it.bin }) { bank ->
                    BankRow(bank, isSelected = bank.bin == selected?.bin, onClick = { onPick(bank) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(bank.shortName, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Text(bank.code, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                bank.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (isSelected) {
            Icon(Icons.Filled.Check, contentDescription = "Đang chọn", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
