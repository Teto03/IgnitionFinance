package com.unimib.ignitionfinance.data.remote.service.utils

object AuthErrors {
    const val INVALID_CREDENTIALS = "Invalid email or password."
    const val NO_ACCOUNT_FOUND = "No account found with this email address."
    const val EMAIL_ALREADY_IN_USE = "Email is already in use by another account."
    const val GENERIC_ERROR = "An unexpected error occurred."
    const val SIGN_IN_FAILED = "Failed to sign in with email and password."
    const val CREATE_USER_FAILED = "Failed to create user with email and password."
    const val RESET_PASSWORD_FAILED = "An unexpected error occurred while resetting password."
}
