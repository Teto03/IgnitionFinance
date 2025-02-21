package com.unimib.ignitionfinance.data.remote.model

import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

data class UserData(
    val name: String = "",
    val surname: String = "",
    val authData: AuthData,
    val settings: Settings,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val cash: String = "0",
    val productList: List<Product> = emptyList(),
    val firstAdded: Boolean = false,
    val dataset: List<DailyReturn> = emptyList(),
    val simulationOutcome: Pair<List<SimulationResult>, Double?>? = null
)