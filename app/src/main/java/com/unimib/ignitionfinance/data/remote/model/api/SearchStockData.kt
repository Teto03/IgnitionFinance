package com.unimib.ignitionfinance.data.remote.model.api

data class SearchStockData(
    val symbol: String,
    val currency: String,
    val matchScore: String
)