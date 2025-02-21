package com.unimib.ignitionfinance.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.unimib.ignitionfinance.R

val NeueHaasGroteskDisplayFontFamily = FontFamily(
    Font(R.font.neue_haas_grotesk_display_regular, FontWeight.Normal),
    Font(R.font.neue_haas_grotesk_display_medium, FontWeight.Medium),
    Font(R.font.neue_haas_grotesk_display_bold, FontWeight.Bold)
)

val Typography = Typography(
    // Body Normal
    bodySmall = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    ),

    // Title Normal
    titleLarge = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.4.sp
    ),

    // Headline Normal
    headlineSmall = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.4.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.4.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.4.sp
    ),

    // Display Normal
    displaySmall = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 54.sp,
        letterSpacing = 0.4.sp
    ),
    displayMedium = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 64.sp,
        lineHeight = 72.sp,
        letterSpacing = 0.4.sp
    ),
    displayLarge = TextStyle(
        fontFamily = NeueHaasGroteskDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 96.sp,
        lineHeight = 108.sp,
        letterSpacing = 0.4.sp
    ),
)

val TypographyMedium = Typography.copy(
    // Body Medium
    bodySmall = Typography.bodySmall.copy(fontWeight = FontWeight.Medium),
    bodyMedium = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
    bodyLarge = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),

    // Title Medium
    titleLarge = Typography.titleLarge.copy(fontWeight = FontWeight.Medium)
)

val TypographyBold = Typography.copy(
    // Headline Bold
    headlineLarge = Typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
    bodySmall = Typography.bodySmall.copy(fontWeight = FontWeight.Bold)
)