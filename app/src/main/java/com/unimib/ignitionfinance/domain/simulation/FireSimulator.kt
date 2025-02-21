package com.unimib.ignitionfinance.domain.simulation

import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object FireSimulator {

    suspend fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): SimulationResult = withContext(Dispatchers.Default) {
        val simulationYears = 100
        val numSimulations = config.settings.numberOfSimulations.toInt()
        val initialInvestedCapital = config.capital.invested
        val initialCashCapital = config.capital.cash
        val loadPercentage = config.settings.expenses.loadPercentage.toDouble()
        val bufferYears = config.settings.intervals.yearsOfBuffer.toDouble()
        val taxRate = config.settings.expenses.taxRatePercentage.toDouble()
        val stampDuty = config.settings.expenses.stampDutyPercentage.toDouble()
        val retirementYear = config.settings.intervals.yearsInPaidRetirement.toInt()
        val cashInterestRate = config.simulationParams.cashInterestRate

        val simulationResults = (0 until numSimulations).map { simIndex ->
            async {
                var portfolio = initialInvestedCapital
                var cash = initialCashCapital
                var buffer = 0.0
                var taxableCapital = initialInvestedCapital * loadPercentage
                var portfolioAtRetirement = portfolio
                var cashAtRetirement = cash
                var bufferAtRetirement = buffer

                for (t in 0 until simulationYears - 1) {
                    val currentWithdrawal = withdrawalMatrix[t][simIndex]

                    cash *= (1.0 + cashInterestRate)

                    var remainingWithdrawal = currentWithdrawal

                    if (cash > 0) {
                        val cashWithdrawal = min(cash, remainingWithdrawal)
                        cash -= cashWithdrawal
                        remainingWithdrawal -= cashWithdrawal
                    }

                    if (remainingWithdrawal > 0) {
                        val condition = if (t > 0) {
                            marketReturnsMatrix[t][simIndex] > marketReturnsMatrix[t - 1][simIndex]
                        } else {
                            true
                        }

                        val withdrawalLimit = if (condition) {
                            remainingWithdrawal * bufferYears
                        } else {
                            remainingWithdrawal
                        }

                        val availableFunds = portfolio
                        val newBuffer = min(availableFunds, withdrawalLimit)
                        val withdrawalBuffer = max(0.0, newBuffer - buffer)

                        val capitalGain = if (portfolio > 0) {
                            withdrawalBuffer * (1.0 - (taxableCapital / portfolio))
                        } else {
                            0.0
                        }
                        val tax = capitalGain * taxRate

                        if (tax != 0.0 && portfolio > 0.0) {
                            taxableCapital *= (1.0 - withdrawalBuffer / portfolio)
                        }
                        portfolio -= withdrawalBuffer
                        buffer = buffer + withdrawalBuffer - tax - remainingWithdrawal
                    }

                    if (portfolio < currentWithdrawal * 1e-6 && cash < currentWithdrawal * 1e-6) {
                        portfolio = 0.0
                    }

                    if (t + 1 == retirementYear) {
                        portfolioAtRetirement = portfolio
                        cashAtRetirement = cash
                        bufferAtRetirement = buffer
                    }

                    val nextMarketReturn = marketReturnsMatrix[t + 1][simIndex]
                    portfolio *= nextMarketReturn

                    val stampDutyAmount = (portfolio + buffer) * stampDuty
                    buffer -= stampDutyAmount
                }

                val success = (portfolioAtRetirement + cashAtRetirement + bufferAtRetirement) > 0
                success
            }
        }.awaitAll()

        val successCount = simulationResults.count { it }
        val successRate = successCount.toDouble() / numSimulations.toDouble()

        SimulationResult(successRate)
    }
}