package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.remote.model.api.ExchangeData
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    suspend fun fetchExchangeData(seriesKey: String): Flow<Result<List<ExchangeData>>>
}