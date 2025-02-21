package com.unimib.ignitionfinance.domain.usecase.fetch

import com.unimib.ignitionfinance.data.remote.model.api.SearchStockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchSearchStockDataUseCase @Inject constructor(
    private val searchStockRepository: SearchStockRepository
) {
    fun execute(ticker: String, apiKey: String): Flow<Result<SearchStockData>> = flow {
        try {
            if (ticker.isEmpty()) {
                throw IllegalArgumentException("Ticker cannot be empty")
            }

            if (apiKey.isEmpty()) {
                throw IllegalArgumentException("API key cannot be empty")
            }

            val searchResult = searchStockRepository.fetchSearchStockData(ticker, apiKey).first()

            searchResult.fold(
                onSuccess = { searchStocks ->
                    val firstMatch = searchStocks.firstOrNull()
                        ?: throw NoSuchElementException("No matching stock found for ticker: $ticker")
                    emit(Result.success(firstMatch))
                },
                onFailure = { exception ->
                    emit(Result.failure(exception))
                }
            )

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}