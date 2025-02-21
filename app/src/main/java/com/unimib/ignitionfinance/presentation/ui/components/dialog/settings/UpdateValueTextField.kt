package com.unimib.ignitionfinance.presentation.ui.components.dialog.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.utils.getTextFieldColors

@Composable
fun UpdateValueTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String?) -> Unit,
    errorMessage: String? = null,
    onDone: (() -> Unit)? = null
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            text = input
            onValueChange(input.ifBlank { null })
        },
        label = { Text(stringResource(id = R.string.dialog_new_value_label)) },
        shape = RoundedCornerShape(56.dp),
        colors = getTextFieldColors(isError = errorMessage != null),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                if (errorMessage == null) {
                    onDone?.invoke()
                }
            }
        ),
        modifier = modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
    )

    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(top = 8.dp)
        )
    }
}