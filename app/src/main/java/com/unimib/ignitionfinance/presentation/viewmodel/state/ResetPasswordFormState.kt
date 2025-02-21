package com.unimib.ignitionfinance.presentation.viewmodel.state

data class ResetPasswordFormState(
    val email: String = "",
    val emailError: String? = null,
    val isValid: Boolean = false
)