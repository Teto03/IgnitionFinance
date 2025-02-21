package com.unimib.ignitionfinance.presentation.ui.components.dialog.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.validation.ProductValidationResult
import com.unimib.ignitionfinance.domain.validation.ProductValidator
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

@Composable
fun NewProductDialog(
    onDismissRequest: () -> Unit,
    onProductConfirmation: ((String?, String?, String?, String?, String?) -> Unit)?,
    dialogTitle: String,
) {
    val defaultSymbol = "SYMBOL"

    var isinInput by remember { mutableStateOf<String?>(null) }
    var tickerInput by remember { mutableStateOf<String?>(null) }
    var purchaseDateInput by remember { mutableStateOf<String?>(null) }
    var amountInput by remember { mutableStateOf<String?>(null) }

    var isinError by remember { mutableStateOf<String?>(null) }
    var tickerError by remember { mutableStateOf<String?>(null) }
    var purchaseDateError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val isinFocusRequester = remember { FocusRequester() }
    val tickerFocusRequester = remember { FocusRequester() }
    val purchaseDateFocusRequester = remember { FocusRequester() }
    val amountFocusRequester = remember { FocusRequester() }

    val isFormValid = remember(isinInput, tickerInput, purchaseDateInput, amountInput) {
        when (ProductValidator.validateNewProductForm(isinInput, tickerInput, purchaseDateInput, amountInput)) {
            is ProductValidationResult.Success -> true
            is ProductValidationResult.Failure -> false
        }
    }

    LaunchedEffect(Unit) {
        isinFocusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onProductConfirmation?.invoke(isinInput, tickerInput, purchaseDateInput, amountInput, defaultSymbol)
                    }
                },
                enabled = isFormValid,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Confirm",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = if (isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Dismiss",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        title = {
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = TypographyMedium.titleLarge.fontWeight
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "ISIN",
                    keyboardType = KeyboardType.Text,
                    onValueChange = { input ->
                        isinInput = input
                        isinError = when (val result = ProductValidator.validateIsin(input)) {
                            is ProductValidationResult.Success -> null
                            is ProductValidationResult.Failure -> result.message
                        }
                    },
                    errorMessage = isinError,
                    focusRequester = isinFocusRequester,
                    nextFocusRequester = tickerFocusRequester
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "TICKER",
                    keyboardType = KeyboardType.Text,
                    onValueChange = { input ->
                        tickerInput = input
                        tickerError = when (val result = ProductValidator.validateTicker(input)) {
                            is ProductValidationResult.Success -> null
                            is ProductValidationResult.Failure -> result.message
                        }
                    },
                    errorMessage = tickerError,
                    focusRequester = tickerFocusRequester,
                    nextFocusRequester = purchaseDateFocusRequester
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Purchase Date",
                    keyboardType = KeyboardType.Text,
                    onValueChange = { input ->
                        purchaseDateInput = input
                        purchaseDateError =
                            when (val result = ProductValidator.validatePurchaseDate(input)) {
                                is ProductValidationResult.Success -> null
                                is ProductValidationResult.Failure -> result.message
                            }
                    },
                    errorMessage = purchaseDateError,
                    focusRequester = purchaseDateFocusRequester,
                    nextFocusRequester = amountFocusRequester
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "€ Amount",
                    keyboardType = KeyboardType.Number,
                    onValueChange = { input ->
                        amountInput = input
                        amountError = when (val result = ProductValidator.validateAmount(input)) {
                            is ProductValidationResult.Success -> null
                            is ProductValidationResult.Failure -> result.message
                        }
                    },
                    errorMessage = amountError,
                    focusRequester = amountFocusRequester,
                    isLastField = true,
                    onDone = {
                        if (isFormValid) {
                            onProductConfirmation?.invoke(isinInput, tickerInput, purchaseDateInput, amountInput, defaultSymbol)
                        }
                    }
                )
            }
        }
    )
}