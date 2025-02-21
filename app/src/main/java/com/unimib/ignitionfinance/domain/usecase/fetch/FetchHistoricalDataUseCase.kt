package com.unimib.ignitionfinance.domain.usecase.fetch

import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.remote.model.api.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val stockRepository: StockRepository,
    private val fetchSearchStockDataUseCase: FetchSearchStockDataUseCase
) {
    fun fetchSingleProductHistory(
        ticker: String,
        symbol: String,
        apiKey: String
    ): Flow<Result<Map<String, StockData>>> = flow {
        try {
            if (apiKey.isEmpty()) {
                throw IllegalArgumentException("API key cannot be empty")
            }

            val actualSymbol = symbol.ifEmpty {
                val searchResult = fetchSearchStockDataUseCase.execute(ticker, apiKey).first()
                searchResult.getOrNull()?.symbol
                    ?: throw NoSuchElementException("Symbol not found for product: $ticker")
            }

            val stockDataResult = stockRepository.fetchStockData(actualSymbol, apiKey).first()
            emit(stockDataResult)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun execute(apiKey: String): Flow<Result<List<Map<String, StockData>>>> = flow {
        try {
            if (apiKey.isEmpty()) {
                throw IllegalArgumentException("API key cannot be empty")
            }

            val productsResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            val products = productsResult.getOrNull() ?: run {
                val error = productsResult.exceptionOrNull() ?: Exception("Failed to get products")
                emit(Result.failure(error))
                return@flow
            }

            if (products.isEmpty()) {
                emit(Result.failure(NoSuchElementException("No products found")))
                return@flow
            }

            val historicalDataList = mutableListOf<Map<String, StockData>>()

            for (product in products) {
                try {
                    val symbol = product.symbol.ifEmpty {
                        val searchResult =
                            fetchSearchStockDataUseCase.execute(product.ticker, apiKey).first()
                        searchResult.getOrNull()?.symbol
                            ?: throw NoSuchElementException("Symbol not found for product: ${product.ticker}")
                    }

                    val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()
                    stockDataResult.fold(
                        onSuccess = { stockData ->
                            historicalDataList.add(stockData)
                        },
                        onFailure = { exception ->
                            emit(Result.failure(exception))
                            return@flow
                        }
                    )
                } catch (e: Exception) {
                    emit(Result.failure(Exception("Error processing product ${product.ticker}: ${e.message}")))
                    return@flow
                }
            }

            if (historicalDataList.isEmpty()) {
                emit(Result.failure(NoSuchElementException("No historical data found for any product")))
            } else {
                emit(Result.success(historicalDataList))
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}