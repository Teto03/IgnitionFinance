package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.model.api.InflationData
import com.unimib.ignitionfinance.data.remote.response.InflationResponse

object InflationMapper {

    fun mapToDomain(inflationResponse: InflationResponse): List<InflationData> {
        return inflationResponse.dataSets.flatMap { inflationDataSet ->
            inflationDataSet.series.flatMap { seriesEntry ->
                val inflationSeries = seriesEntry.value
                inflationSeries.observations.mapNotNull { observation ->
                    val date = observation.key
                    val values = observation.value
                    values.firstOrNull()?.toString()?.toDoubleOrNull()?.let { inflationRate ->
                        InflationData(year = date, inflationRate = inflationRate)
                    }
                }
            }
        }
    }
}

