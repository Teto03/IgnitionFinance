package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.runtime.*
import com.unimib.ignitionfinance.presentation.ui.components.dialog.product.NewProductDialog
import com.unimib.ignitionfinance.presentation.ui.components.dialog.settings.UpdateValueDialog

@Composable
fun DialogManager(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: ((String?) -> Unit)? = null,
    onProductConfirmation: ((String?, String?, String?, String?, String?) -> Unit)? = null,
    dialogTitle: String,
    prefix: String,
    firstAdded: Boolean = false
) {
    if (showDialog) {
        if (!firstAdded) {
            requireNotNull(onConfirmation) { "onConfirmation must be provided when showing UpdateValueDialog" }

            UpdateValueDialog(
                onDismissRequest = onDismissRequest,
                onConfirmation = onConfirmation,
                dialogTitle = dialogTitle,
                prefix = prefix
            )
        } else {
            requireNotNull(onProductConfirmation) { "onProductConfirmation must be provided when showing NewProductDialog" }

            NewProductDialog(
                onDismissRequest = onDismissRequest,
                onProductConfirmation = onProductConfirmation,
                dialogTitle = dialogTitle
            )
        }
    }
}