package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.remote.model.api.SearchStockData

object SearchStockMapper {
    fun mapToDomain(response: SearchStockResponse): List<SearchStockData> {
        return response.bestMatches.map { match ->
            SearchStockData(
                symbol = match.symbol,
                currency = match.currency,
                matchScore = match.matchScore
            )
        }
    }
}
