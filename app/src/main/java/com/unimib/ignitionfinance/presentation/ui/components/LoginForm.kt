package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginForm(
    viewModel: LoginScreenViewModel,
    navController: NavController,
    name: String,
    surname: String,
) {
    val loginState by viewModel.loginState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val emailFocused = remember { mutableStateOf(false) }
    val passwordFocused = remember { mutableStateOf(false) }
    val isFabFocused = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(loginState) {
        viewModel.handleLoginState(
            loginState = loginState,
            name = name,
            surname = surname,
            onNavigateToPortfolio = {
                navController.navigate(Destinations.PortfolioScreen.route) {
                    popUpTo(Destinations.LoginScreen.route) { inclusive = true }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        CustomTextField(
            value = formState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = stringResource(id = R.string.label_email),
            isError = formState.emailError != null,
            nextFocusRequester = passwordFocusRequester,
            imeAction = ImeAction.Next,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .onFocusChanged {
                    emailFocused.value = it.isFocused
                    if (it.isFocused) isFabFocused.value = false
                }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = formState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = stringResource(id = R.string.label_password),
                isError = formState.passwordError != null,
                imeAction = ImeAction.Done,
                isPasswordField = true,
                onImeActionPerformed = {
                    if (formState.isValid) {
                        focusManager.clearFocus()
                        viewModel.login()
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1.0f)
                    .onFocusChanged {
                        passwordFocused.value = it.isFocused
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
                        isFabFocused.value = true
                        viewModel.login()
                    }
                },
                containerColor = if (formState.isValid) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                contentColor = if (formState.isValid) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondary
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedError = when {
            isFabFocused.value && loginState is UiState.Error -> (loginState as UiState.Error).message
            emailFocused.value -> formState.emailError
            passwordFocused.value -> formState.passwordError
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

        TextButton(
            onClick = { navController.navigate(Destinations.ResetPasswordScreen.route) },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                stringResource(id = R.string.forgot_password_button),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        TextButton(
            onClick = { navController.navigate(Destinations.RegistrationScreen.route) },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                stringResource(id = R.string.create_account_button),
                style = TypographyMedium.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}