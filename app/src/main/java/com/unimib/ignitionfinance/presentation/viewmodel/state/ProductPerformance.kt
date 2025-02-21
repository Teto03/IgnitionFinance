package com.unimib.ignitionfinance.presentation.viewmodel.state

import java.math.BigDecimal

data class ProductPerformance(
    val ticker: String,
    val purchaseDate: String,
    val purchasePrice: BigDecimal,
    val currentDate: String,
    val currentPrice: BigDecimal,
    val percentageChange: BigDecimal,
    val currency: String
)