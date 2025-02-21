package com.unimib.ignitionfinance.presentation.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit

@Composable
fun SettingsCardTitle(
    label: String,
    title: String,
    titleFontSize: TextUnit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = titleFontSize),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
