package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.unimib.ignitionfinance.presentation.ui.components.settings.ExpandableIcon
import com.unimib.ignitionfinance.presentation.ui.components.settings.SettingsCardTitle

@Composable
fun InputCardHeader(
    label: String,
    title: String,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    titleFontSize: TextUnit = MaterialTheme.typography.displaySmall.fontSize
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsCardTitle(label = label, title = title, titleFontSize = titleFontSize)
        ExpandableIcon(isExpanded = isExpanded, onClick = onCardClicked)
    }
}
