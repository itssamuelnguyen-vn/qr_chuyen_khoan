package com.samuelcruz.qrchuyenkhoan.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.samuelcruz.qrchuyenkhoan.R

/** Be Vietnam Pro: an open-source typeface drawn for Vietnamese (licenses/BeVietnamPro-OFL.txt). */
val BeVietnamPro = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
)

private val typography = Typography().run {
    copy(
        headlineMedium = headlineMedium.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.SemiBold),
        titleSmall = titleSmall.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.SemiBold),
        bodyLarge = bodyLarge.copy(fontFamily = BeVietnamPro),
        bodyMedium = bodyMedium.copy(fontFamily = BeVietnamPro),
        bodySmall = bodySmall.copy(fontFamily = BeVietnamPro),
        labelLarge = labelLarge.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.SemiBold),
        labelMedium = labelMedium.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.SemiBold),
        labelSmall = labelSmall.copy(fontFamily = BeVietnamPro, fontWeight = FontWeight.Medium),
    )
}

private val Light = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF134E4A),
    background = Color(0xFFF4F7F6),
    surface = Color(0xFFF4F7F6),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFEEF3F2),
    onSurface = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF5B6472),
    outline = Color(0xFFB8C2C0),
    outlineVariant = Color(0xFFE2E8E6),
    error = Color(0xFFDC2626),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF0B1514),
    surface = Color(0xFF0B1514),
    surfaceContainerLow = Color(0xFF14211F),
    surfaceContainer = Color(0xFF14211F),
    surfaceContainerHigh = Color(0xFF1B2B29),
    surfaceContainerHighest = Color(0xFF22322F),
    onSurface = Color(0xFFE6F0EE),
    onSurfaceVariant = Color(0xFF9DB0AD),
    outline = Color(0xFF4A5C59),
    outlineVariant = Color(0xFF253533),
    error = Color(0xFFF87171),
)

@Composable
fun QrTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, typography = typography, content = content)
}
