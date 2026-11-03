package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuelcruz.qrchuyenkhoan.qr.Bank
import com.samuelcruz.qrchuyenkhoan.qr.BankStyle

/** The app's signature teal gradient (header, main button). */
val BrandGradient = Brush.linearGradient(listOf(Color(0xFF0F766E), Color(0xFF14B8A6)))

fun bankColor(bank: Bank): Color = Color(BankStyle.color(bank))

/** A rounded square in the bank's colour with its letters, standing in for a logo. */
@Composable
fun BankBadge(bank: Bank, size: Dp = 44.dp) {
    val letters = BankStyle.monogram(bank)
    val fontSize = when (letters.length) {
        1, 2 -> size.value * 0.36f
        3 -> size.value * 0.30f
        else -> size.value * 0.24f
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .background(
                Brush.linearGradient(listOf(bankColor(bank), bankColor(bank).copy(alpha = 0.78f))),
                RoundedCornerShape(size * 0.28f),
            ),
    ) {
        Text(letters, color = Color.White, fontWeight = FontWeight.Bold, fontSize = fontSize.sp, maxLines = 1)
    }
}

/** A white card with a small title, grouping related fields. */
@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp,
            )
            content()
        }
    }
}

/** The big call-to-action: gradient pill with an icon. */
@Composable
fun GradientButton(
    text: String,
    icon: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = BrandGradient,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp),
        modifier = modifier.height(56.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(brush, RoundedCornerShape(18.dp)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (loading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
