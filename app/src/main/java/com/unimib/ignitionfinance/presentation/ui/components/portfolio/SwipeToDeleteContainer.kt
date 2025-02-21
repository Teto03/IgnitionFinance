package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    swipeEnabled: Boolean = true,
    isSwiped: Boolean,
    onSwiped: () -> Unit,
    onResetSwipe: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = stringResource(id = R.string.label_swipe_animation),
    )
    val deleteThreshold = with(LocalDensity.current) { 80.dp.toPx() }
    val iconSize = 24.dp

    LaunchedEffect(isSwiped) {
        if (!isSwiped && offsetX != 0f) {
            offsetX = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(LocalDensity.current) { deleteThreshold.toDp() })
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = stringResource(id = R.string.delete_product),
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .offset(x = (iconSize / 2))
                        .clickable { onDelete() }
                )
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .then(
                    if (swipeEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    offsetX = (offsetX + dragAmount).coerceIn(-deleteThreshold, 0f)
                                    if (offsetX != 0f) {
                                        onSwiped()
                                    }
                                },
                                onDragEnd = {
                                    offsetX = if (abs(offsetX) < deleteThreshold / 2) {
                                        onResetSwipe()
                                        0f
                                    } else {
                                        onSwiped()
                                        -deleteThreshold
                                    }
                                }
                            )
                        }
                    } else Modifier
                )
                .clickable {
                    if (offsetX != 0f) {
                        offsetX = 0f
                        onResetSwipe()
                    }
                }
                .fillMaxWidth()
        ) {
            content()
        }
    }
}