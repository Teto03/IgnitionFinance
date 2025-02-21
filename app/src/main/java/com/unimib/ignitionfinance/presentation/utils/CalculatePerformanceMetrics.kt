package com.unimib.ignitionfinance.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.presentation.ui.screens.Quadruple
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.O)
fun calculatePerformanceMetrics(products: List<Product>): Triple<Double, Pair<String, Double>, Pair<String, Double>>? {
    if (products.isEmpty()) return null

    val performances = products.mapNotNull { product ->
        val performance = product.averagePerformance.toDoubleOrNull()
        val amount = product.amount.replace("[^0-9.]".toRegex(), "").toDoubleOrNull()
        val holdingPeriodYears = calculateHoldingPeriodInYears(product.purchaseDate)

        if (performance != null && amount != null && amount > 0) {
            val annualizedReturn = if (holdingPeriodYears > 0) {
                (1 + (performance / 100)).pow(1.0 / holdingPeriodYears) - 1
            } else {
                performance / 100
            }

            val annualizedReturnPercentage = annualizedReturn * 100

            Quadruple(
                product.ticker,
                annualizedReturnPercentage,
                amount,
                holdingPeriodYears
            )
        } else null
    }

    if (performances.isEmpty()) return null

    val totalWeightedAmount = performances.sumOf { it.third * it.fourth }
    val weightedAveragePerformance = performances.sumOf {
        (it.second * it.third * it.fourth) / totalWeightedAmount
    }

    val bestPerformer = performances.maxByOrNull { it.second }
        ?.let { it.first to it.second } ?: ("" to 0.0)

    val worstPerformer = performances.minByOrNull { it.second }
        ?.let { it.first to it.second } ?: ("" to 0.0)

    return Triple(weightedAveragePerformance, bestPerformer, worstPerformer)
}