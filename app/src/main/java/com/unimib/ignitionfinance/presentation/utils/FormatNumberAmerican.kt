package com.unimib.ignitionfinance.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatNumberAmerican(input: String): String {
    return try {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        val formatter = DecimalFormat("#,###.##", symbols).apply {
            isGroupingUsed = true
            maximumFractionDigits = 2
        }

        input.toDoubleOrNull()?.let { formatter.format(it) } ?: input
    } catch (_: NumberFormatException) {
        input
    }
}