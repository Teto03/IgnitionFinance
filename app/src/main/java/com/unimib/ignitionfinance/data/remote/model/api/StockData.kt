package com.unimib.ignitionfinance.data.remote.model.api

import java.math.BigDecimal

data class StockData(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long,
    val percentageChange: BigDecimal
)