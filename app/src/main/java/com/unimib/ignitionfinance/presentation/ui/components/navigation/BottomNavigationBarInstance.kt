package com.unimib.ignitionfinance.presentation.ui.components.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unimib.ignitionfinance.R
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.model.BottomNavigationItemModel

@Composable
fun BottomNavigationBarInstance(navController: NavController) {
    BottomNavigationBar(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        items = listOf(
            BottomNavigationItemModel(
                iconRes = R.drawable.avd_outline_add_notes_24,
                label = stringResource(id = R.string.portfolio_label),
                destination = Destinations.PortfolioScreen.route,
                contentDescription = stringResource(id = R.string.portfolio_label)
            ),
            BottomNavigationItemModel(
                iconRes = R.drawable.avd_outline_donut_large_24,
                label = stringResource(id = R.string.summary_label),
                destination = Destinations.SummaryScreen.route,
                contentDescription = stringResource(id = R.string.summary_label)
            ),
            BottomNavigationItemModel(
                iconRes = R.drawable.avd_outline_analytics_24,
                label = stringResource(id = R.string.simulation_label),
                destination = Destinations.SimulationScreen.route,
                contentDescription = stringResource(id = R.string.simulation_label)
            )
        ),
        navController = navController
    )
}
