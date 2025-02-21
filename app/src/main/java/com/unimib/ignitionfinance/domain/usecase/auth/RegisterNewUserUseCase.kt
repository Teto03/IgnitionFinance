package com.unimib.ignitionfinance.domain.usecase.auth

import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.RegistrationFormState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterNewUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    private fun validateRegistrationForm(
        name: String,
        surname: String,
        email: String,
        password: String
    ): RegistrationFormState {
        val nameValidation = RegistrationValidator.validateName(name)
        val surnameValidation = RegistrationValidator.validateSurname(surname)
        val emailValidation = RegistrationValidator.validateEmail(email)
        val passwordValidation = RegistrationValidator.validatePassword(password)

        val isValid = RegistrationValidator.validateRegistrationForm(
            name, surname, email, password
        ) is RegistrationValidationResult.Success

        return RegistrationFormState(
            name = name,
            surname = surname,
            email = email,
            password = password,
            nameError = (nameValidation as? RegistrationValidationResult.Failure)?.message,
            surnameError = (surnameValidation as? RegistrationValidationResult.Failure)?.message,
            emailError = (emailValidation as? RegistrationValidationResult.Failure)?.message,
            passwordError = (passwordValidation as? RegistrationValidationResult.Failure)?.message,
            isValid = isValid
        )
    }

    fun validateForm(name: String, surname: String, email: String, password: String): RegistrationFormState {
        return validateRegistrationForm(name, surname, email, password)
    }

    suspend fun execute(email: String, password: String): Flow<Result<AuthData>> {
        return authRepository.createUserWithEmailAndPassword(email, password)
    }
}