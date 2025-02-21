package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.remote.model.api.SearchStockData
import kotlinx.coroutines.flow.Flow

interface SearchStockRepository {
    suspend fun fetchSearchStockData(ticker: String, apiKey: String): Flow<Result<List<SearchStockData>>>
}