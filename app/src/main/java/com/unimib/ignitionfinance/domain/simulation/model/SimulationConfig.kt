package com.unimib.ignitionfinance.domain.simulation.model

import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Settings

data class SimulationConfig(
    val dataset: List<DailyReturn>,
    val settings: Settings,
    val historicalInflation: Map<Int, Double>,
    val capital: Capital,
    val simulationParams: SimulationParams
)