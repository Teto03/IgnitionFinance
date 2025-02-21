package com.unimib.ignitionfinance.presentation.viewmodel.state

data class RegistrationFormState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val surnameError: String? = null,
    val isValid: Boolean = false
)