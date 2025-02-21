package com.unimib.ignitionfinance.domain.usecase.auth

import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.domain.validation.LoginValidationResult
import com.unimib.ignitionfinance.domain.validation.LoginValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.LoginFormState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    private fun validateLoginForm(email: String, password: String): LoginFormState {
        val emailValidation = LoginValidator.validateEmail(email)
        val passwordValidation = LoginValidator.validatePassword(password)

        return LoginFormState(
            email = email,
            password = password,
            emailError = (emailValidation as? LoginValidationResult.Failure)?.message,
            passwordError = (passwordValidation as? LoginValidationResult.Failure)?.message,
            isValid = LoginValidator.validateLoginForm(email, password) is LoginValidationResult.Success
        )
    }

    fun validateForm(email: String, password: String): LoginFormState {
        return validateLoginForm(email, password)
    }

    suspend fun execute(email: String, password: String): Flow<Result<AuthData>> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }
}