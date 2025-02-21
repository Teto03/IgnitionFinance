package com.unimib.ignitionfinance.domain.simulation.model

data class SimulationParams(
    val cashInterestRate: Double = 0.01,
    val averageInflation: Double = 0.03,
    val daysPerYear: Int = 253
)