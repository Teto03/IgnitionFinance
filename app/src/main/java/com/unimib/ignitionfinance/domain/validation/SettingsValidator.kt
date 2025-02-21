package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object SettingsValidator {
    fun validate(value: String?, prefix: String): InputValidationResult {
        if (value.isNullOrBlank()) return InputValidationResult.Success

        return when {
            !ValidationRules.validateInput(value, prefix) -> {
                when (prefix) {
                    "€" -> InputValidationResult.Failure(ValidationErrors.Input.POSITIVE_EURO)
                    "%" -> InputValidationResult.Failure(ValidationErrors.Input.PERCENTAGE_RANGE)
                    "YRS" -> InputValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
                    "N°" -> InputValidationResult.Failure(ValidationErrors.Input.NUMBER_RANGE)
                    else -> InputValidationResult.Failure(String.format(ValidationErrors.Input.INVALID_INPUT, prefix))
                }
            }
            else -> InputValidationResult.Success
        }
    }
}

sealed class InputValidationResult {
    data object Success : InputValidationResult()
    data class Failure(val message: String) : InputValidationResult()
}
