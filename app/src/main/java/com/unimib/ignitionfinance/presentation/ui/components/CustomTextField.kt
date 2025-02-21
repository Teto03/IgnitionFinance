package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.utils.getTextFieldColors
import com.unimib.ignitionfinance.R

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    nextFocusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPasswordField: Boolean = false,
    modifier: Modifier = Modifier,
    onImeActionPerformed: (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text(label) },
            shape = RoundedCornerShape(56.dp),
            colors = getTextFieldColors(isError = isError),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (!isError && nextFocusRequester != null) {
                        nextFocusRequester.requestFocus()
                    }
                },
                onDone = {
                    if (!isError) {
                        keyboardController?.hide()
                        onImeActionPerformed?.invoke()
                    }
                }
            ),
            visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else visualTransformation,
            trailingIcon = if (isPasswordField) {
                {
                    val iconRes = if (passwordVisible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                    val contentDescription = if (passwordVisible)
                        stringResource(id = R.string.password_hide)
                    else
                        stringResource(id = R.string.password_show)
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = contentDescription,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else null
        )
    }
}