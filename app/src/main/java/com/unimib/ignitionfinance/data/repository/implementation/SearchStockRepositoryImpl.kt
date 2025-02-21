package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.remote.model.api.SearchStockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.GsonBuilder

private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

class SearchStockRepositoryImpl @Inject constructor(
    private val searchStockService: SearchStockService,
    private val searchStockMapper: SearchStockMapper
) : SearchStockRepository {

    override suspend fun fetchSearchStockData(ticker: String, apiKey: String): Flow<Result<List<SearchStockData>>> = flow {
        try {

            val response = searchStockService.getSearchStockData(ticker = ticker, apiKey = apiKey)

            if (response.isSuccessful) {
                val searchStockData = response.body()

                gson.toJson(searchStockData)

                if (searchStockData?.bestMatches != null) {
                    emit(Result.success(searchStockMapper.mapToDomain(searchStockData)))
                } else {
                    emit(Result.failure(Throwable("Error: `bestMatches` is null in API response for $ticker")))
                }
            } else {
                emit(Result.failure(Throwable("Failed to fetch search stock data: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}