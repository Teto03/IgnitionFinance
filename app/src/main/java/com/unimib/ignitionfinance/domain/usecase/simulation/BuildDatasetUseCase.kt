package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.remote.model.api.StockData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchHistoricalDataUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
import com.unimib.ignitionfinance.domain.utils.DailyReturnCalculator
import com.unimib.ignitionfinance.domain.validation.DatasetValidationResult
import com.unimib.ignitionfinance.domain.validation.DatasetValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class BuildDatasetUseCase @Inject constructor(
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase,
    private val stockRepository: StockRepository,
    private val dailyReturnCalculator: DailyReturnCalculator,
    private val addDatasetToDatabaseUseCase: AddDatasetToDatabaseUseCase,
    private val getProductListUseCase: GetProductListUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String): Flow<Result<List<DailyReturn>>> = flow {
        try {
            val productsResult = getProductListUseCase.execute(apiKey).first()
            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()
            val historicalDataList = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val dailyReturns = when (DatasetValidator.validate(historicalDataList)) {
                is DatasetValidationResult.Success -> {
                    if (products.isEmpty()) {
                        val sp500Result = stockRepository.fetchStockData("SPY", BuildConfig.ALPHAVANTAGE_API_KEY).first()
                        val sp500Data: Map<String, StockData> = sp500Result.getOrElse {
                            emit(Result.failure(it))
                            return@flow
                        }
                        dailyReturnCalculator.calculateDailyReturns(listOf(sp500Data), products)
                    } else {
                        dailyReturnCalculator.calculateDailyReturns(historicalDataList, products)
                    }
                }
                is DatasetValidationResult.Failure -> {
                    val sp500Result = stockRepository.fetchStockData("SPY", BuildConfig.ALPHAVANTAGE_API_KEY).first()
                    val sp500Data: Map<String, StockData> = sp500Result.getOrElse {
                        emit(Result.failure(it))
                        return@flow
                    }
                    dailyReturnCalculator.calculateDailyReturns(listOf(sp500Data), products)
                }
            }

            val saveResult = addDatasetToDatabaseUseCase.execute(dailyReturns).first()
            if (saveResult.isFailure) {
                emit(Result.failure(saveResult.exceptionOrNull() ?: Exception("Unknown error")))
                return@flow
            }

            emit(Result.success(dailyReturns))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}