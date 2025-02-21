package com.unimib.ignitionfinance.presentation.ui.components.intro

import androidx.compose.runtime.Composable
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.theme.PrimaryWhite
import kotlinx.coroutines.delay

@Composable
fun AnimatedText(text: String, visible: Boolean) {
    val initialTextColor = MaterialTheme.colorScheme.secondary
    val changedTextColor = PrimaryWhite

    val animatedText = remember { mutableIntStateOf(0) }

    if (visible) {
        LaunchedEffect(Unit) {
            for (i in text.indices) {
                delay(18)
                animatedText.intValue = i + 1
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)),
        exit = fadeOut(tween(1000))
    ) {
        Text(
            text = buildAnnotatedString {
                for (i in text.indices) {
                    withStyle(
                        style = SpanStyle(
                            color = if (i < animatedText.intValue) changedTextColor else initialTextColor
                        )
                    ) {
                        append(text[i])
                    }
                }
            },
            modifier = Modifier.padding(start = 16.dp, end = 112.dp, bottom = 160.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
