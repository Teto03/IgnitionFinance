package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.viewmodel.ResetPasswordScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState

@Composable
fun ResetPasswordForm(
    viewModel: ResetPasswordScreenViewModel,
    navController: NavController,
) {
    val resetState by viewModel.resetState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val emailFocusRequester = remember { FocusRequester() }
    val emailFocused = remember { mutableStateOf(false) }
    val isFabFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(resetState) {
        if (resetState is UiState.Success) {
            navController.navigate(Destinations.LoginScreen.route) {
                popUpTo(Destinations.ResetPasswordScreen.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = formState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = stringResource(id = R.string.label_email),
                isError = formState.emailError != null,
                imeAction = ImeAction.Done,
                onImeActionPerformed = {
                    if (formState.isValid) {
                        focusManager.clearFocus()
                        viewModel.reset()
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(emailFocusRequester)
                    .weight(1f)
                    .onFocusChanged {
                        emailFocused.value = it.isFocused
                        if (it.isFocused) isFabFocused.value = false
                    }
            )

            CustomFAB(
                modifier = Modifier.padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.login_FAB_description),
                onClick = {
                    if (formState.isValid) {
                        focusManager.clearFocus()
                        viewModel.reset()
                        isFabFocused.value = true
                    }
                },
                containerColor = if (formState.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                contentColor = if (formState.isValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedError = when {
            isFabFocused.value && resetState is UiState.Error -> (resetState as UiState.Error).message
            emailFocused.value -> formState.emailError
            else -> null
        }

        selectedError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, end = 64.dp)
            )
        }
    }
}