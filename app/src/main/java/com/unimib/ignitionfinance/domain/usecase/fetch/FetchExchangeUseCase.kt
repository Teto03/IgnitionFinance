package com.unimib.ignitionfinance.domain.usecase.fetch

import com.unimib.ignitionfinance.data.remote.model.api.ExchangeData
import com.unimib.ignitionfinance.data.repository.interfaces.ExchangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchExchangeUseCase @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) {
    fun execute(targetCurrency: String): Flow<Result<ExchangeData>> = flow {
        try {
            if (targetCurrency.isEmpty()) {
                throw IllegalArgumentException("Target currency cannot be empty")
            }

            exchangeRepository.fetchExchangeData(targetCurrency)
                .collect { result ->
                    result.fold(
                        onSuccess = { exchangeList ->
                            if (exchangeList.isNotEmpty()) {
                                emit(Result.success(exchangeList.first()))
                            } else {
                                emit(Result.failure(NoSuchElementException("No exchange data found")))
                            }
                        },
                        onFailure = { exception ->
                            emit(Result.failure(exception))
                        }
                    )
                }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}