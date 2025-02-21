package com.unimib.ignitionfinance.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun calculateHoldingPeriodInYears(purchaseDate: String): Double {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val now = LocalDate.now()

    try {
        val parsedDate = LocalDate.parse(purchaseDate, dateFormatter)
        val daysBetween = ChronoUnit.DAYS.between(parsedDate, now)
        return daysBetween / 365.0
    } catch (_: DateTimeParseException) {
        return 0.0
    }
}