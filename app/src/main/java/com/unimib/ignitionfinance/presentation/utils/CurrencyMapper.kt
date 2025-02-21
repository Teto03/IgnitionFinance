package com.unimib.ignitionfinance.presentation.utils

object CurrencyMapper {

    private val currencySymbols = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "CHF" to "₣",
        "JPY" to "¥",
        "GBP" to "£",
        "GBX" to "p",
        "CAD" to "$",
        "AUD" to "$",
        "NZD" to "$",
        "CNY" to "¥",
        "HKD" to "$",
        "INR" to "₹",
        "RUB" to "₽",
        "BRL" to "R$",
        "ZAR" to "R",
        "KRW" to "₩",
        "SGD" to "$",
        "SEK" to "kr",
        "NOK" to "kr",
        "DKK" to "kr",
        "PLN" to "zł",
        "TRY" to "₺",
        "MXN" to "$",
        "ARS" to "$",
        "CLP" to "$",
        "COP" to "$",
        "PEN" to "S/",
        "IDR" to "Rp",
        "MYR" to "RM",
        "THB" to "฿",
        "VND" to "₫",
        "AED" to "د.إ",
        "SAR" to "﷼",
        "QAR" to "﷼",
        "KWD" to "د.ك",
        "BHD" to ".د.ب",
        "OMR" to "ر.ع.",
        "JOD" to "د.ا",
        "LBP" to "ل.ل",
        "ILS" to "₪",
        "CZK" to "Kč",
        "HUF" to "Ft",
        "RON" to "lei",
        "BGN" to "лв",
        "HRK" to "kn",
        "ISK" to "kr",
        "EGP" to "£",
        "NGN" to "₦",
        "PKR" to "₨",
        "UAH" to "₴",
        "DZD" to "دج",
        "MAD" to "د.م."
    )

    fun mapCurrencyToSymbol(currencyCode: String): String {
        return currencySymbols[currencyCode.uppercase()] ?: currencyCode
    }
}
