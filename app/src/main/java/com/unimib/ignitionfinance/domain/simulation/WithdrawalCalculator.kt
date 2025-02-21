package com.unimib.ignitionfinance.domain.simulation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object WithdrawalCalculator {
    private const val MONTHS_PER_YEAR = 13


    suspend fun calculateWithdrawals(
        initialWithdrawal: Double,
        yearsWithoutPension: Int,
        pensionWithdrawal: Double,
        inflationMatrix: Array<DoubleArray>
    ): Array<DoubleArray> {
        val simulationLength = inflationMatrix.size
        if (simulationLength == 0) return arrayOf()
        val numSimulations = inflationMatrix[0].size

        val annualInitialWithdrawal = initialWithdrawal * MONTHS_PER_YEAR
        val annualPensionWithdrawal = pensionWithdrawal * MONTHS_PER_YEAR

        val withdrawals = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }
        val coef = DoubleArray(numSimulations) { 1.0 }

        for (sim in 0 until numSimulations) {
            withdrawals[0][sim] = annualInitialWithdrawal
        }

        withContext(Dispatchers.Default) {
            val jobs1 = List(numSimulations) { sim ->
                async {
                    for (t in 1..yearsWithoutPension) {
                        withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                        coef[sim] *= (1 + inflationMatrix[t][sim])
                    }
                }
            }
            jobs1.awaitAll()

            if (yearsWithoutPension + 1 < simulationLength) {
                val jobs2 = List(numSimulations) { sim ->
                    async {
                        withdrawals[yearsWithoutPension + 1][sim] =
                            annualPensionWithdrawal * coef[sim] * (1 + inflationMatrix[yearsWithoutPension + 1][sim])
                    }
                }
                jobs2.awaitAll()
            }

            val jobs3 = List(numSimulations) { sim ->
                async {
                    for (t in (yearsWithoutPension + 2) until simulationLength) {
                        withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                    }
                }
            }
            jobs3.awaitAll()
        }
        return withdrawals
    }
}