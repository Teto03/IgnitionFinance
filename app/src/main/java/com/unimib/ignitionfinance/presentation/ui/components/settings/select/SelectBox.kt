package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel

@Composable
fun SelectBox(
    model: SelectBoxModel,
    onTextSelected: (String) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    boxHeight: Dp = 152.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight)
            .background(MaterialTheme.colorScheme.background)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
    ) {
        Text(
            text = model.text,
            color = MaterialTheme.colorScheme.secondary,
            style = textStyle,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            model.displayedTexts.forEach { displayedText ->
                SelectBoxBody(
                    text = displayedText,
                    isSelected = model.selectedText == displayedText,
                    onClick = {
                        onTextSelected(displayedText)
                    }
                )
            }
        }
    }
}
