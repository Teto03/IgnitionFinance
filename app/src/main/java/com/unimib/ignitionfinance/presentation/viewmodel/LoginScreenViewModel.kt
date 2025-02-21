package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.domain.usecase.auth.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.LoginUserUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.LoginFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val addUserToDatabaseUseCase: AddUserToDatabaseUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthData>> = _loginState

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    fun updateEmail(email: String) {
        _formState.update { currentState ->
            loginUserUseCase.validateForm(email, currentState.password)
        }
    }

    fun updatePassword(password: String) {
        _formState.update { currentState ->
            loginUserUseCase.validateForm(currentState.email, password)
        }
    }

    fun login() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _loginState.value = UiState.Loading
                    loginUserUseCase.execute(currentState.email, currentState.password)
                        .collect { result ->
                            _loginState.value = when {
                                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                                result.isFailure -> UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "No details available"
                                )
                                else -> UiState.Idle
                            }
                        }
                } catch (e: Exception) {
                    _loginState.value = UiState.Error(
                        e.localizedMessage ?: "Unexpected error occurred during login"
                    )
                }
            }
        }
    }

    fun handleLoginState(
        loginState: UiState<AuthData>,
        name: String,
        surname: String,
        onNavigateToPortfolio: () -> Unit
    ) {
        viewModelScope.launch {
            when (loginState) {
                is UiState.Success -> {
                    addUserToDatabaseUseCase.handleUserStorage(
                        loginState.data,
                        name,
                        surname
                    ).collect { result ->
                        result.fold(
                            onSuccess = { onNavigateToPortfolio() },
                            onFailure = { /* Handle error */ }
                        )
                    }
                }
                is UiState.Error -> {
                    val errorMessage = loginState.message
                    println("Login error: $errorMessage")
                }
                else -> {}
            }
        }
    }
}
