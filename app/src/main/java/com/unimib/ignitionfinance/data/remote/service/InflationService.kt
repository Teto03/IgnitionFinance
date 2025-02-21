package com.unimib.ignitionfinance.data.remote.service

import com.unimib.ignitionfinance.data.remote.response.InflationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InflationService {

    @GET("service/data/{dataflow}/{seriesKey}")
    suspend fun getInflationData(
        @Path("dataflow") dataflow: String = "ECB,ICP,1.0",
        @Path("seriesKey") seriesKey: String = "A.IT.N.000000.4.AVR",
        @Query("format") format: String = "jsondata"
    ): Response<InflationResponse>
}
