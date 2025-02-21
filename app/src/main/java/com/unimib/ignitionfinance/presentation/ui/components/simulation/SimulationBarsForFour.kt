package com.unimib.ignitionfinance.presentation.ui.components.simulation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SimulationBar(
    val percentage: Double,
    val capital: String
)

const val SIMULATION_MAX_BAR_HEIGHT = 0.9f

@Composable
fun SimulationBars(
    results: List<SimulationBar>
) {
    val barColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.onTertiary
    )

    Row(
        modifier = Modifier
            .padding(top = 36.dp)
            .height(300.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        results.forEachIndexed { index, result ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (result.percentage <= 0.3) {
                    Text(
                        text = "${(result.percentage * 100).toInt()}%",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontSize = if (index == 0) 16.sp else 14.sp,
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight((result.percentage * SIMULATION_MAX_BAR_HEIGHT).toFloat())
                        .background(
                            barColors.getOrElse(index) { MaterialTheme.colorScheme.primaryContainer },
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    if (result.percentage > 0.3) {
                        Text(
                            text = "${(result.percentage * 100).toInt()}%",
                            fontSize = if (index == 0) 16.sp else 14.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        )
                    }
                }
                Text(
                    text = result.capital,
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 14.sp,
                    color = if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun SimulationBarsForFour(
    capital1: String, percentage1: Double,
    capital2: String, percentage2: Double,
    capital3: String, percentage3: Double,
    capital4: String, percentage4: Double
) {
    val simulationResults = listOf(
        SimulationBar(percentage = percentage1, capital = capital1),
        SimulationBar(percentage = percentage2, capital = capital2),
        SimulationBar(percentage = percentage3, capital = capital3),
        SimulationBar(percentage = percentage4, capital = capital4)
    )
    SimulationBars(results = simulationResults)
}