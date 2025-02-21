package com.unimib.ignitionfinance.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryWhite,
    secondary = SecondaryLightGray,
    background = BackgroundBlack,
    surface = BackgroundDarkGray,
    onPrimary = BackgroundBlack,
    onSecondary = HighlightDarkGray,
    tertiary = ChartColumn1DarkGray,
    onTertiary = ChartColumn2DarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    secondary = SecondaryGray,
    background = BackgroundWhite,
    surface = BackgroundGray,
    onPrimary = BackgroundWhite,
    onSecondary = HighlightGray,
    tertiary = ChartColumn1Gray,
    onTertiary = ChartColumn2Gray
)

@Composable
fun IgnitionFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}