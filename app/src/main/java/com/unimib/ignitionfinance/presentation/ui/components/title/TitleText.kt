package com.unimib.ignitionfinance.presentation.ui.components.title

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyBold

@Composable
fun TitleText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    CustomText(
        text = text,
        color = color,
        style = TypographyBold.headlineLarge,
        modifier = modifier,
        maxLines = 2
    )
}
