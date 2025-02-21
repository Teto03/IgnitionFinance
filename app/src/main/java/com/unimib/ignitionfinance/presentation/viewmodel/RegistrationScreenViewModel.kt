package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.domain.usecase.auth.RegisterNewUserUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.RegistrationFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val registerNewUserUseCase: RegisterNewUserUseCase,
) : ViewModel() {

    private val _registrationState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val registrationState: StateFlow<UiState<AuthData>> = _registrationState

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState

    fun updateName(name: String) {
        _formState.update { currentState ->
            registerNewUserUseCase.validateForm(
                name,
                currentState.surname,
                currentState.email,
                currentState.password
            )
        }
    }

    fun updateSurname(surname: String) {
        _formState.update { currentState ->
            registerNewUserUseCase.validateForm(
                currentState.name,
                surname,
                currentState.email,
                currentState.password
            )
        }
    }

    fun updateEmail(email: String) {
        _formState.update { currentState ->
            registerNewUserUseCase.validateForm(
                currentState.name,
                currentState.surname,
                email,
                currentState.password
            )
        }
    }

    fun updatePassword(password: String) {
        _formState.update { currentState ->
            registerNewUserUseCase.validateForm(
                currentState.name,
                currentState.surname,
                currentState.email,
                password
            )
        }
    }

    fun register() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _registrationState.value = UiState.Loading
                    registerNewUserUseCase.execute(currentState.email, currentState.password)
                        .collect { result ->
                            _registrationState.value = when {
                                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                                result.isFailure -> UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Registration failed"
                                )
                                else -> UiState.Idle
                            }
                        }
                } catch (e: Exception) {
                    _registrationState.value = UiState.Error(
                        e.localizedMessage ?: "Unexpected error occurred during registration"
                    )
                }
            }
        }
    }
}