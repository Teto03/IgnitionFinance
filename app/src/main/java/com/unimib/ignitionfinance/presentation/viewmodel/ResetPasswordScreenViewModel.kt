package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.auth.ResetPasswordUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.ResetPasswordFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState: StateFlow<UiState<Unit>> = _resetState

    private val _formState = MutableStateFlow(ResetPasswordFormState())
    val formState: StateFlow<ResetPasswordFormState> = _formState

    fun updateEmail(email: String) {
        _formState.update { _ ->
            resetPasswordUseCase.validateForm(email)
        }
    }

    fun reset() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _resetState.value = UiState.Loading
                    resetPasswordUseCase.execute(currentState.email)
                        .collect { result ->
                            _resetState.value = when {
                                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                                result.isFailure -> UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Reset password failed"
                                )
                                else -> UiState.Idle
                            }
                        }
                } catch (e: Exception) {
                    _resetState.value = UiState.Error(
                        e.localizedMessage ?: "Unexpected error occurred during password reset"
                    )
                }
            }
        }
    }
}