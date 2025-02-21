package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class StockResponse(
    @SerializedName("Meta Data")
    val metaData: MetaData,
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, TimeSeriesData>
)

data class MetaData(
    @SerializedName("1. Information") val information: String,
    @SerializedName("2. Symbol") val symbol: String,
    @SerializedName("3. Last Refreshed") val lastRefreshed: String,
    @SerializedName("4. Output Size") val outputSize: String,
    @SerializedName("5. Time Zone") val timeZone: String
)

data class TimeSeriesData(
    @SerializedName("1. open") val open: BigDecimal,
    @SerializedName("2. high") val high: BigDecimal,
    @SerializedName("3. low") val low: BigDecimal,
    @SerializedName("4. close") val close: BigDecimal,
    @SerializedName("5. volume") val volume: Long
)
