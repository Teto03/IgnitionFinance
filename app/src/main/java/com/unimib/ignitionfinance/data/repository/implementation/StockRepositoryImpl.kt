package com.unimib.ignitionfinance.data.repository.implementation

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.model.api.StockData
import com.unimib.ignitionfinance.data.remote.response.TimeSeriesData
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val stockService: StockService,
    private val stockApiMapper: StockMapper,
    @ApplicationContext private val context: Context
) : StockRepository {

    override suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>> = flow {
        try {
            val response = stockService.getStockData(symbol = symbol, apiKey = apiKey)
            if (response.isSuccessful) {
                val stockData = response.body()
                if (stockData != null) {
                    var finalStockData = stockData
                    if (symbol.uppercase() == "SPY") {
                        val extraJson = readAssetFile()
                        val type = object : TypeToken<Map<String, TimeSeriesData>>() {}.type
                        val extraData: Map<String, TimeSeriesData> = Gson().fromJson(extraJson, type)

                        val filteredExtraData = extraData.filterKeys { date -> date < "1999-11-01" }

                        val mergedTimeSeries = stockData.timeSeries.toMutableMap().apply {
                            putAll(filteredExtraData)
                        }

                        finalStockData = stockData.copy(timeSeries = mergedTimeSeries)
                    }
                    val mappedData = stockApiMapper.mapToDomain(finalStockData)
                    emit(Result.success(mappedData))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Failed to fetch stock data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)


    private fun readAssetFile(): String {
        return context.assets.open("extra_data.json").bufferedReader().use { it.readText() }
    }
}