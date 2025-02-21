package com.unimib.ignitionfinance.presentation.ui.components.navigation

import androidx.compose.runtime.*

data class NavigationState(
    val previousDestination: String,
    val updatePreviousDestination: (String) -> Unit
)

@Composable
fun rememberNavigationState(
    initialDestination: String
): NavigationState {
    var previousDestination by remember { mutableStateOf(initialDestination) }

    return NavigationState(
        previousDestination = previousDestination,
        updatePreviousDestination = { previousDestination = it }
    )
}