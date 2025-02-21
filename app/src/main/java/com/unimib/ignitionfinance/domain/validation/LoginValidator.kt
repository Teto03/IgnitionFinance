package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object LoginValidator {
    fun validateEmail(email: String?): LoginValidationResult {
        return when {
            email.isNullOrBlank() -> LoginValidationResult.Success
            !ValidationRules.validateEmail(email) -> LoginValidationResult.Failure(ValidationErrors.Login.EMAIL_ERROR)
            else -> LoginValidationResult.Success
        }
    }

    fun validatePassword(password: String?): LoginValidationResult {
        return when {
            password.isNullOrBlank() -> LoginValidationResult.Success
            !ValidationRules.validatePassword(password) -> LoginValidationResult.Failure(
                ValidationErrors.Login.PASSWORD_ERROR)
            else -> LoginValidationResult.Success
        }
    }

    fun validateLoginForm(email: String?, password: String?): LoginValidationResult {
        return when {
            !ValidationRules.validateLoginForm(email, password) -> LoginValidationResult.Failure(
                ValidationErrors.Login.INVALID_FORM)
            else -> LoginValidationResult.Success
        }
    }
}

sealed class LoginValidationResult {
    data object Success : LoginValidationResult()
    data class Failure(val message: String) : LoginValidationResult()
}

