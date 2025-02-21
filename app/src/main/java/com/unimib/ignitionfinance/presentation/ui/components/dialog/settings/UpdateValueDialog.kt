package com.unimib.ignitionfinance.presentation.ui.components.dialog.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.domain.validation.InputValidationResult
import com.unimib.ignitionfinance.domain.validation.SettingsValidator

@Composable
fun UpdateValueDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String?) -> Unit,
    dialogTitle: String,
    prefix: String
) {
    var textInput by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isInputValid = errorMessage == null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isInputValid) {
                        onConfirmation(textInput)
                    }
                },
                enabled = isInputValid,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    stringResource(id = R.string.dialog_confirm),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = if (isInputValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
                    stringResource(id = R.string.dialog_dismiss),
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
                UpdateValueTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = SettingsValidator.validate(input, prefix)
                        errorMessage = if (validationResult is InputValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage,
                    onDone = {
                        if (isInputValid) {
                            onConfirmation(textInput)
                        }
                    }
                )
            }
        },
    )
}