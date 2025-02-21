package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import com.unimib.ignitionfinance.data.remote.model.api.StockData

object DatasetValidator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(stockDataList: List<Map<String, StockData>>): DatasetValidationResult {
        for (productData in stockDataList) {
            if (productData.isNotEmpty()) {
                var hasValidDate = false

                for (dateStr in productData.keys) {
                    if (dateStr.isNotBlank()) {
                        if (ValidationRules.isDateOlderThan(dateStr, 10)) {
                            hasValidDate = true
                            break
                        }
                    }
                }

                if (hasValidDate) {
                    return DatasetValidationResult.Success
                }
            }
        }
        return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
    }
}

sealed class DatasetValidationResult {
    data object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}