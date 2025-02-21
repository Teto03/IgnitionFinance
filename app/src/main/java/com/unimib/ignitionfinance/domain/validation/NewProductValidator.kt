package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object ProductValidator {
    fun validateIsin(isin: String?): ProductValidationResult {
        return when {
            isin.isNullOrBlank() -> ProductValidationResult.Success
            !ValidationRules.validateIsin(isin) -> ProductValidationResult.Failure(ValidationErrors.NewProduct.INVALID_ISIN)
            else -> ProductValidationResult.Success
        }
    }

    fun validateTicker(ticker: String?): ProductValidationResult {
        return when {
            ticker.isNullOrBlank() -> ProductValidationResult.Success
            !ValidationRules.validateTicker(ticker) -> ProductValidationResult.Failure(ValidationErrors.NewProduct.INVALID_TICKER)
            else -> ProductValidationResult.Success
        }
    }

    fun validatePurchaseDate(date: String?): ProductValidationResult {
        return when {
            date.isNullOrBlank() -> ProductValidationResult.Success
            !ValidationRules.validatePurchaseDate(date) -> ProductValidationResult.Failure(ValidationErrors.NewProduct.INVALID_PURCHASE_DATE)
            else -> ProductValidationResult.Success
        }
    }

    fun validateAmount(amount: String?): ProductValidationResult {
        return when {
            amount.isNullOrBlank() -> ProductValidationResult.Success
            !ValidationRules.validateAmount(amount) -> ProductValidationResult.Failure(ValidationErrors.NewProduct.INVALID_AMOUNT)
            else -> ProductValidationResult.Success
        }
    }

    fun validateNewProductForm(
        isin: String?,
        ticker: String?,
        date: String?,
        amount: String?
    ): ProductValidationResult {
        return when {
            !ValidationRules.validateNewProductDialog(isin, ticker, date, amount) ->
                ProductValidationResult.Failure(ValidationErrors.NewProduct.INVALID_FORM)
            else -> ProductValidationResult.Success
        }
    }
}

sealed class ProductValidationResult {
    data object Success : ProductValidationResult()
    data class Failure(val message: String) : ProductValidationResult()
}