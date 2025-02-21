package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun AnimatedInputValue(
    inputValue: String,
    textStyle: TextStyle
) {
    Row(
        modifier = Modifier.graphicsLayer(clip = false)
    ) {
        inputValue.forEachIndexed { index, char ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(char, index) {
                delay(index * 30L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { 10 },
                    animationSpec = tween(durationMillis = 100)
                ) + fadeIn(animationSpec = tween(durationMillis = 100)),
                exit = ExitTransition.None
            ) {
                Text(
                    text = char.toString(),
                    style = textStyle,
                    color = MaterialTheme.colorScheme.primary,
                    softWrap = false
                )
            }
        }
    }
}