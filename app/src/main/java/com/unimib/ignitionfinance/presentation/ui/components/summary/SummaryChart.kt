package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun SummaryChart(
    cash: Double,
    invested: Double
) {
    val investedColor = MaterialTheme.colorScheme.primary
    val cashColor = MaterialTheme.colorScheme.onSecondary
    val total = cash + invested

    val (investedPercentage, cashPercentage) = if (total > 0) {
        Pair(
            (invested / total) * 100,
            (cash / total) * 100
        )
    } else {
        Pair(100.0, 0.0)
    }

    val sweepAngle = remember { Animatable(0f) }

    LaunchedEffect(investedPercentage, cashPercentage) {
        sweepAngle.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Canvas(
                modifier = Modifier.fillMaxWidth()
            ) {
                val strokeWidth = 20f
                val canvasSize = size.width
                val radius = (canvasSize - strokeWidth) / 2

                val investedSweepAngle = (investedPercentage.toFloat() / 100f * sweepAngle.value)
                val cashSweepAngle = (cashPercentage.toFloat() / 100f * sweepAngle.value)

                drawArc(
                    color = investedColor,
                    startAngle = -90f,
                    sweepAngle = investedSweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(radius * 2, radius * 2)
                )

                drawArc(
                    color = cashColor,
                    startAngle = -90f + investedSweepAngle,
                    sweepAngle = cashSweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
    }
}