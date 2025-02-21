package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

@Composable
fun CustomFAB(
    onClick: (() -> Unit) = {},
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: Painter? = null,
    contentDescription: String? = null,
    fabSize: Dp = 56.dp,
    iconSize: Dp = 24.dp,
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.LongPress,
    elevation: Dp = 0.dp
) {
    val hapticFeedback = LocalHapticFeedback.current
    FloatingActionButton(
        onClick = {
            hapticFeedback.performHapticFeedback(hapticFeedbackType)
            onClick()
        },
        modifier = modifier.size(fabSize),
        containerColor = containerColor,
        contentColor = contentColor,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation)
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

