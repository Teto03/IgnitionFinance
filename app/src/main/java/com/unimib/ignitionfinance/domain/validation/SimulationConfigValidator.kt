package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.data.remote.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.remote.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.remote.model.user.settings.Withdrawals
import com.unimib.ignitionfinance.domain.simulation.model.Capital
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationParams

object SimulationConfigValidator {

    fun validate(config: SimulationConfig): List<String> {
        val errors = mutableListOf<String>()

        validateDataset(config.dataset, errors)
        validateCapital(config.capital, errors)
        validateSettings(config.settings, errors)
        validateHistoricalData(config.historicalInflation, errors)
        validateSimulationParams(config.simulationParams, errors)

        return errors
    }

    private fun validateDataset(dataset: List<DailyReturn>, errors: MutableList<String>) {
        if (dataset.isEmpty()) {
            errors.add("Dataset is empty")
        }
    }

    private fun validateCapital(capital: Capital, errors: MutableList<String>) {
        if (capital.total <= 0) {
            errors.add("Total capital must be greater than 0")
        }
        if (capital.invested < 0) {
            errors.add("Invested capital cannot be negative")
        }
        if (capital.cash < 0) {
            errors.add("Cash amount cannot be negative")
        }
        if (capital.cashPercentage !in 0.0..1.0) {
            errors.add("Cash percentage must be between 0 and 1")
        }
    }

    private fun validateSettings(settings: Settings, errors: MutableList<String>) {
        validateWithdrawals(settings.withdrawals, errors)
        validateExpenses(settings.expenses, errors)
        validateIntervals(settings.intervals, errors)
        validateSimulationCount(settings.numberOfSimulations, errors)
        validateInflationModel(settings.inflationModel, errors)
    }

    private fun validateWithdrawals(withdrawals: Withdrawals, errors: MutableList<String>) {
        withdrawals.withPension.toDoubleOrNull()?.let {
            if (it < 0) errors.add("Pension withdrawal amount cannot be negative")
        } ?: errors.add("Invalid pension withdrawal amount")

        withdrawals.withoutPension.toDoubleOrNull()?.let {
            if (it < 0) errors.add("Pre-pension withdrawal amount cannot be negative")
        } ?: errors.add("Invalid pre-pension withdrawal amount")
    }

    private fun validateExpenses(expenses: Expenses, errors: MutableList<String>) {
        expenses.taxRatePercentage.toDoubleOrNull()?.let {
            if (it !in 0.0..100.0) errors.add("Tax rate must be between 0 and 100")
        } ?: errors.add("Invalid tax rate")

        expenses.stampDutyPercentage.toDoubleOrNull()?.let {
            if (it !in 0.0..100.0) errors.add("Stamp duty must be between 0 and 100")
        } ?: errors.add("Invalid stamp duty")

        expenses.loadPercentage.toDoubleOrNull()?.let {
            if (it !in 0.0..100.0) errors.add("Load percentage must be between 0 and 100")
        } ?: errors.add("Invalid load percentage")
    }

    private fun validateIntervals(intervals: Intervals, errors: MutableList<String>) {
        intervals.yearsInFIRE.toIntOrNull()?.let {
            if (it <= 0) errors.add("FIRE years must be greater than 0")
        } ?: errors.add("Invalid FIRE years")

        intervals.yearsInPaidRetirement.toIntOrNull()?.let {
            if (it < 0) errors.add("Paid retirement years cannot be negative")
        } ?: errors.add("Invalid paid retirement years")

        intervals.yearsOfBuffer.toIntOrNull()?.let {
            if (it < 0) errors.add("Buffer years cannot be negative")
        } ?: errors.add("Invalid buffer years")
    }

    private fun validateSimulationCount(numberOfSimulations: String, errors: MutableList<String>) {
        numberOfSimulations.toIntOrNull()?.let {
            if (it <= 0) errors.add("Number of simulations must be greater than 0")
            if (it > 10000) errors.add("Number of simulations cannot exceed 10000")
        } ?: errors.add("Invalid number of simulations")
    }

    private fun validateInflationModel(inflationModel: String, errors: MutableList<String>) {
        val validModels = setOf("fixed", "NORMAL", "SCALE", "LOGNORMAL")
        if (inflationModel !in validModels) {
            errors.add("Invalid inflation model. Must be one of: ${validModels.joinToString()}")
        }
    }

    private fun validateHistoricalData(historicalInflation: Map<Int, Double>, errors: MutableList<String>) {
        if (historicalInflation.isEmpty()) {
            errors.add("Historical inflation data is empty")
        }

        historicalInflation.forEach { (year, rate) ->
            if (rate.isNaN() || rate.isInfinite()) {
                errors.add("Invalid inflation rate for year $year")
            }
        }
    }

    private fun validateSimulationParams(params: SimulationParams, errors: MutableList<String>) {
        if (params.cashInterestRate < 0 || params.cashInterestRate > 1) {
            errors.add("Cash interest rate must be between 0 and 1")
        }
        if (params.averageInflation < 0 || params.averageInflation > 1) {
            errors.add("Average inflation must be between 0 and 1")
        }
        if (params.daysPerYear <= 0) {
            errors.add("Days per year must be greater than 0")
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}