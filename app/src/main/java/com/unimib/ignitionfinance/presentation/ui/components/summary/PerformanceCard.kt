package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

@Composable
fun PerformanceCard(
    averagePerformance: Double,
    bestPerformer: Pair<String, Double>,
    worstPerformer: Pair<String, Double>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(id = R.string.performance_title),
                    style = TypographyMedium.bodyLarge
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PerformanceLegend(
                    icon = painterResource(id = R.drawable.ssid_chart_24),
                    title = stringResource(id = R.string.performance_average),
                    percentage = "${if (averagePerformance >= 0) "+" else ""}${averagePerformance.toInt()}%",
                    backgroundColor = MaterialTheme.colorScheme.onSecondary,
                    iconColor = MaterialTheme.colorScheme.primary
                )

                PerformanceLegend(
                    icon = painterResource(id = R.drawable.thumb_up_24),
                    title = stringResource(id = R.string.performance_best),
                    percentage = "${bestPerformer.first} (${if (bestPerformer.second >= 0) "+" else ""}${bestPerformer.second.toInt()}%)",
                    backgroundColor = MaterialTheme.colorScheme.onSecondary,
                    iconColor = MaterialTheme.colorScheme.primary
                )

                PerformanceLegend(
                    icon = painterResource(id = R.drawable.thumb_down_24),
                    title = stringResource(id = R.string.performance_worst),
                    percentage = "${worstPerformer.first} (${if (worstPerformer.second >= 0) "+" else ""}${worstPerformer.second.toInt()}%)",
                    backgroundColor = MaterialTheme.colorScheme.onSecondary,
                    iconColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}