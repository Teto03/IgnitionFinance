package com.unimib.ignitionfinance.data.remote.service

import com.unimib.ignitionfinance.data.remote.response.ExchangeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeService {

    @GET("service/data/{dataflow}/{seriesKey}")
    suspend fun getExchangeRate(
        @Path("dataflow") dataflow: String = "ECB,EXR,1.0",
        @Path("seriesKey") seriesKey: String,
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1
    ): Response<ExchangeResponse>
}
