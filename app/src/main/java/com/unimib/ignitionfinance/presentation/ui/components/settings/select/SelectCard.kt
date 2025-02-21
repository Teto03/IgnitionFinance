package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel

@Composable
fun SelectCard(
    label: String,
    title: String,
    model: SelectBoxModel,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    onTextSelected: (String) -> Unit
) {
    val cardHeight = animateDpAsState(
        targetValue = if (isExpanded) {
            val bodyHeight = 104.dp + 24.dp
            bodyHeight + 152.dp
        } else {
            104.dp
        },
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight.value)
            .clickable(onClick = onCardClicked, indication = null, interactionSource = remember { MutableInteractionSource() }),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SelectCardHeader(
                label = label,
                title = title,
                isExpanded = isExpanded,
                onCardClicked = onCardClicked
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                SelectCardBody(
                    model = model,
                    onTextSelected = onTextSelected
                )
            }
        }
    }
}