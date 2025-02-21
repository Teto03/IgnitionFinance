package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object ResetValidator {
    fun validateEmail(email: String?): ResetValidationResult {
        return when {
            email.isNullOrBlank() -> ResetValidationResult.Success
            !ValidationRules.validateEmail(email) -> ResetValidationResult.Failure(ValidationErrors.Reset.EMAIL_ERROR)
            else -> ResetValidationResult.Success
        }
    }

    fun validateResetForm(email: String?): ResetValidationResult {
        return when {
            !ValidationRules.validateResetForm(email) -> ResetValidationResult.Failure(
                ValidationErrors.Reset.INVALID_FORM)
            else -> ResetValidationResult.Success
        }
    }
}
sealed class ResetValidationResult {
    data object Success : ResetValidationResult()
    data class Failure(val message: String) : ResetValidationResult()
}