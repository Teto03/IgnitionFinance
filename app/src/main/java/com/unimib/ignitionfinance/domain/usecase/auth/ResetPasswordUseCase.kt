package com.unimib.ignitionfinance.domain.usecase.auth

import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.domain.validation.ResetValidationResult
import com.unimib.ignitionfinance.domain.validation.ResetValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.ResetPasswordFormState
import kotlinx.coroutines.flow.Flow

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    private fun validateResetPasswordForm(email: String): ResetPasswordFormState {
        val emailValidation = ResetValidator.validateEmail(email)

        return ResetPasswordFormState(
            email = email,
            emailError = (emailValidation as? ResetValidationResult.Failure)?.message,
            isValid = ResetValidator.validateResetForm(email) is ResetValidationResult.Success
        )
    }

    fun validateForm(email: String): ResetPasswordFormState {
        return validateResetPasswordForm(email)
    }

    suspend fun execute(email: String): Flow<Result<Unit>> {
        return authRepository.resetPassword(email)
    }
}