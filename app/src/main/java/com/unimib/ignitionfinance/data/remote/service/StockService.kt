package com.unimib.ignitionfinance.data.remote.service

import com.unimib.ignitionfinance.data.remote.response.StockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockService {

    @GET("query")
    suspend fun getStockData(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("outputsize") outputSize: String = "full",
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): Response<StockResponse>
}