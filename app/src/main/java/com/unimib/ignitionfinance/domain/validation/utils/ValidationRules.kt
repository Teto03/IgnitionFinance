package com.unimib.ignitionfinance.domain.validation.utils

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

object ValidationRules {
    private object Configs {
        val EMAIL_PATTERN: Pattern = Patterns.EMAIL_ADDRESS

        val PASSWORD_RULES = PasswordRule(
            minLength = 8,
            requireUppercase = true,
            requireLowercase = true,
            requireDigit = true,
            specialCharacters = "@#\$%^&?!+=",
            allowSingleWordOnly = true
        )

        val NAME_RULES = NameRule(
            minLength = 3,
            allowSingleWordOnly = true
        )

        val TICKER_PATTERN = Regex("^[A-Z]{1,5}$")
        val ISIN_PATTERN = Regex("^[A-Z]{2}[A-Z0-9]{9}\\d$")
        val DATE_PATTERNS = listOf(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd/MM/yy",
            "dd-MM/yy"
        ).map { SimpleDateFormat(it, Locale.getDefault()) }
    }

    data class PasswordRule(
        val minLength: Int,
        val requireUppercase: Boolean = false,
        val requireLowercase: Boolean = false,
        val requireDigit: Boolean = false,
        val specialCharacters: String = "",
        val allowSingleWordOnly: Boolean = false
    )

    data class NameRule(
        val minLength: Int,
        val allowSingleWordOnly: Boolean = false
    )

    private fun String.isSingleWord(): Boolean = this.trim().split(" ").size == 1

    private fun String.validatePassword(rules: PasswordRule): Boolean {
        if (this.isBlank()) return false

        return (this.length >= rules.minLength) &&
                (!rules.requireUppercase || this.any { it.isUpperCase() }) &&
                (!rules.requireLowercase || this.any { it.isLowerCase() }) &&
                (!rules.requireDigit || this.any { it.isDigit() }) &&
                (rules.specialCharacters.isEmpty() || this.any { it in rules.specialCharacters }) &&
                (!rules.allowSingleWordOnly || this.isSingleWord())
    }

    private fun String.validateName(rules: NameRule): Boolean {
        if (this.isBlank()) return false

        return (this.length >= rules.minLength) &&
                (!rules.allowSingleWordOnly || this.isSingleWord())
    }

    fun validateName(name: String): Boolean {
        return name.validateName(Configs.NAME_RULES)
    }

    fun validatePassword(password: String): Boolean {
        return password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateInput(value: String?, prefix: String): Boolean {
        if (value.isNullOrBlank()) return true

        return when (prefix) {
            "€" -> value.toDoubleOrNull()?.let { it >= 0 } == true
            "%" -> value.toDoubleOrNull()?.let { it in 0.0..100.0 } == true
            "YRS" -> value.toIntOrNull()?.let { it < 100 } == true
            "N°" -> value.toIntOrNull()?.let { it in 1..10000 } == true
            else -> false
        }
    }

    fun validateEmail(email: String, requireSingleWord: Boolean = false): Boolean {
        return email.isNotBlank() &&
                Configs.EMAIL_PATTERN.matcher(email).matches() &&
                (!requireSingleWord || email.isSingleWord())
    }

    fun validateTicker(ticker: String?): Boolean {
        if (ticker.isNullOrBlank()) return false
        return Configs.TICKER_PATTERN.matches(ticker.trim())
    }

    fun validateIsin(isin: String?): Boolean {
        if (isin.isNullOrBlank()) return false
        return Configs.ISIN_PATTERN.matches(isin.trim())
    }

    fun validatePurchaseDate(date: String?): Boolean {
        if (date.isNullOrBlank()) return false

        val trimmedDate = date.trim()
        return Configs.DATE_PATTERNS.any { formatter ->
            try {
                formatter.isLenient = false // Ensures strict date parsing
                formatter.parse(trimmedDate)
                true
            } catch (_: ParseException) {
                false
            }
        }
    }

    fun validateAmount(amount: String?): Boolean {
        if (amount.isNullOrBlank()) return false
        return amount.trim().toDoubleOrNull()?.let { it > 0 } == true
    }

    fun validateLoginForm(email: String?, password: String?): Boolean {
        return !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                validateEmail(email) &&
                password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateRegistrationForm(
        name: String?,
        surname: String?,
        email: String?,
        password: String?
    ): Boolean {
        return !name.isNullOrBlank() &&
                !surname.isNullOrBlank() &&
                !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                name.validateName(Configs.NAME_RULES) &&
                surname.validateName(Configs.NAME_RULES) &&
                validateEmail(email, requireSingleWord = true) &&
                password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateNewProductDialog(
        isin: String?,
        ticker: String?,
        date: String?,
        amount: String?
    ): Boolean {
        return !isin.isNullOrBlank() &&
                !ticker.isNullOrBlank() &&
                !date.isNullOrBlank() &&
                !amount.isNullOrBlank() &&
                validateIsin(isin) &&
                validateTicker(ticker) &&
                validatePurchaseDate(date) &&
                validateAmount(amount)
    }

    fun validateResetForm(email: String?): Boolean {
        return !email.isNullOrBlank() && validateEmail(email)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDateOlderThan(dateStr: String, yearsThreshold: Long): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            val date = LocalDate.parse(dateStr, formatter)
            val currentDate = LocalDate.now()
            date.isBefore(currentDate.minusYears(yearsThreshold))
        } catch (e: Exception) {
            false
        }
    }

}