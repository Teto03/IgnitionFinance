package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

data class SimulationScreenState(
    val netWorth: Double? = null,
    val currentPortfolioValue: Double = 0.0,
    val portfolioValueState: UiState<Double> = UiState.Idle,
    val initialInvestment: Double = 0.0,
    val simulationDuration: Int = 1,
    val parametersState: UiState<Any> = UiState.Idle,
    val simulationResult: SimulationResult? = null,
    val simulationState: UiState<Pair<List<SimulationResult>, Double>> = UiState.Idle,
    val lastSimulationResult: Pair<List<SimulationResult>, Double?>? = null
)