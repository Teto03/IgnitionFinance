package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object RegistrationValidator {
    fun validateName(name: String?): RegistrationValidationResult {
        return when {
            name.isNullOrBlank() -> RegistrationValidationResult.Success
            !ValidationRules.validateName(name) -> RegistrationValidationResult.Failure(
                ValidationErrors.Registration.INVALID_NAME)
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateSurname(surname: String?): RegistrationValidationResult {
        return when {
            surname.isNullOrBlank() -> RegistrationValidationResult.Success
            !ValidationRules.validateName(surname) -> RegistrationValidationResult.Failure(
                ValidationErrors.Registration.INVALID_SURNAME)
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateEmail(email: String?): RegistrationValidationResult {
        return when {
            email.isNullOrBlank() -> RegistrationValidationResult.Success
            !ValidationRules.validateEmail(email) -> RegistrationValidationResult.Failure(
                ValidationErrors.Registration.EMAIL_ERROR)
            else -> RegistrationValidationResult.Success
        }
    }

    fun validatePassword(password: String?): RegistrationValidationResult {
        return when {
            password.isNullOrBlank() -> RegistrationValidationResult.Success
            !ValidationRules.validatePassword(password) -> RegistrationValidationResult.Failure(
                ValidationErrors.Registration.PASSWORD_ERROR)
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateRegistrationForm(name: String?, surname: String?, email: String?, password: String?): RegistrationValidationResult {
        return when {
            !ValidationRules.validateRegistrationForm(name, surname, email, password) -> RegistrationValidationResult.Failure(
                ValidationErrors.Registration.INVALID_FORM)
            else -> RegistrationValidationResult.Success
        }
    }
}
sealed class RegistrationValidationResult {
    data object Success : RegistrationValidationResult()
    data class Failure(val message: String) : RegistrationValidationResult()
}

