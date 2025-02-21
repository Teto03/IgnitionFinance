package com.unimib.ignitionfinance.data.remote.model.user

import java.math.BigDecimal


data class DailyReturn(
    val date: String,
    val weightedReturn: BigDecimal
)