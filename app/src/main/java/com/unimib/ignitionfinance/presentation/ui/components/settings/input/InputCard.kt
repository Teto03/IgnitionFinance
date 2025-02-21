package com.unimib.ignitionfinance.presentation.ui.components.settings.input

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
import com.unimib.ignitionfinance.presentation.model.InputBoxModel

@Composable
fun InputCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    inputBoxModelList: List<InputBoxModel>,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    val cardInputBoxHeight = 64.dp
    val spacerHeight = 24.dp

    val cardHeight = animateDpAsState(
        targetValue = if (isExpanded) {
            val totalInputBoxHeight = cardInputBoxHeight * inputBoxModelList.size
            val totalSpacerHeight = spacerHeight * (inputBoxModelList.size)
            totalInputBoxHeight + totalSpacerHeight + 104.dp
        } else {
            104.dp
        },
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight.value)
            .clickable(
                onClick = onCardClicked,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
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
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputCardHeader(
                label = label,
                title = title,
                isExpanded = isExpanded,
                onCardClicked = onCardClicked
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                InputCardBody(inputBoxModelList = inputBoxModelList)
            }
        }
    }
}