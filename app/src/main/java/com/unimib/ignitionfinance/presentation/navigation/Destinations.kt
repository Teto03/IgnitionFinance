package com.unimib.ignitionfinance.presentation.navigation

sealed class Destinations(val route: String) {
    data object IntroScreen : Destinations("intro_screen")
    data object PortfolioScreen : Destinations("portfolio_screen")
    data object SimulationScreen : Destinations("simulation_screen")
    data object SummaryScreen : Destinations("summary_screen")
    data object SettingsScreen : Destinations("settings_screen")
    data object LoginScreen : Destinations("login_screen?name={name}&surname={surname}") {
        fun createRoute(name: String?, surname: String?): String {
            val nameQuery = name?.let { "name=$it" } ?: ""
            val surnameQuery = surname?.let { "surname=$it" } ?: ""
            val query = listOf(nameQuery, surnameQuery).filter { it.isNotEmpty() }.joinToString("&")
            return "login_screen?$query"
        }
    }
    data object RegistrationScreen : Destinations("register_screen")
    data object ResetPasswordScreen : Destinations("reset_password_screen") //
}
