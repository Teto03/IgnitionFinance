package com.unimib.ignitionfinance.data.remote.service

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchStockService {

    @GET("query")
    suspend fun getSearchStockData(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") ticker: String,
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): Response<SearchStockResponse>
}