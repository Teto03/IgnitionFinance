package com.unimib.ignitionfinance.domain.usecase.networth.invested

import com.unimib.ignitionfinance.BuildConfig
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class GetUserInvestedUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase
) {
    fun execute(): Flow<Result<Double>> = flow {
        val productListResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()

        val productList = productListResult.getOrNull()
            ?: throw IllegalStateException("Failed to get product list")

        var totalAmount = 0.0
        productList.forEach { product ->
            val amount = product.amount.toDoubleOrNull() ?: 0.0
            totalAmount += amount
        }

        emit(Result.success(totalAmount))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> emit(Result.failure(e))
        }
    }
}