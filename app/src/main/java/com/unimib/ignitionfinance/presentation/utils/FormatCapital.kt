package com.unimib.ignitionfinance.presentation.utils

import java.util.Locale

fun formatCapital(value: Double): String {
    return when {
        value < 1_000 -> "$value"
        value < 1_000_000 -> {
            val thousands = value / 1000.0
            if (thousands >= 100) {
                "${thousands.toInt()}k"
            } else {
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}k"
                } else {
                    String.format(Locale.US, "%.1fk", thousands)
                }
            }
        }
        value < 100_000_000 -> {
            val millions = value / 1_000_000.0
            String.format(Locale.US, "%.1fM", millions)
        }
        value < 1_000_000_000 -> {
            val millions = value / 1_000_000
            "${millions}M"
        }
        else -> {
            val billions = value / 1_000_000_000.0
            if (billions < 100) String.format(Locale.US, "%.1fMLD", billions)
            else "${billions.toInt()}MLD"
        }
    }
}