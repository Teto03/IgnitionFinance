package com.unimib.ignitionfinance.domain.simulation.model

data class Capital(
    val invested: Double,
    val cash: Double
) {
    val total: Double
        get() = invested + cash

    val cashPercentage: Double
        get() = if (total > 0) cash / total else 0.0
}