package com.unimib.ignitionfinance.domain.usecase.settings

import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.data.remote.model.user.settings.Withdrawals
import com.unimib.ignitionfinance.data.remote.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.remote.model.user.settings.Intervals
import javax.inject.Inject

class SetDefaultSettingsUseCase @Inject constructor(){

    fun execute(): Settings {
        return Settings(
            withdrawals = Withdrawals(
                withPension = "----",
                withoutPension = "----"
            ),
            inflationModel = "LOGNORMAL",
            expenses = Expenses(
                taxRatePercentage = "26",
                stampDutyPercentage = "0.2",
                loadPercentage = "100"
            ),
            intervals = Intervals(
                yearsInFIRE = "----",
                yearsInPaidRetirement = "----",
                yearsOfBuffer = "----"
            ),
            numberOfSimulations = "----"
        )
    }
}