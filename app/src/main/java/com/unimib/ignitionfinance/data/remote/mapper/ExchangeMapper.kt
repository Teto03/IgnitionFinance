package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.model.api.ExchangeData
import com.unimib.ignitionfinance.data.remote.response.ExchangeResponse

object ExchangeMapper {

    fun mapToDomain(exchangeResponse: ExchangeResponse): List<ExchangeData> {
        return exchangeResponse.dataSets.flatMap { exchangeDataSet ->
            exchangeDataSet.series.flatMap { seriesEntry ->
                val exchangeSeries = seriesEntry.value
                exchangeSeries.observations.mapNotNull { observation ->
                    val date = observation.key
                    val values = observation.value
                    values.firstOrNull()?.toString()?.toDoubleOrNull()?.let { inflationRate ->
                        ExchangeData(date = date, exchangeRate = inflationRate)
                    }
                }
            }
        }
    }
}
