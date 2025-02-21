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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.viewmodel.RegistrationScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState

@Composable
fun RegistrationForm(
    viewModel: RegistrationScreenViewModel,
    navController: NavController,
) {
    val registrationState by viewModel.registrationState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val currentFocused = remember { mutableStateOf<String?>(null) }
    val isFabFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    LaunchedEffect(registrationState) {
        if (registrationState is UiState.Success) {
            navController.navigate(
                Destinations.LoginScreen.createRoute(
                    name = formState.name,
                    surname = formState.surname
                )
            ) {
                popUpTo(Destinations.RegistrationScreen.route) { inclusive = true }
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
                value = formState.name,
                onValueChange = { viewModel.updateName(it) },
                label = stringResource(id = R.string.label_name),
                isError = formState.nameError != null,
                nextFocusRequester = surnameFocusRequester,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(nameFocusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            currentFocused.value = "name"
                            isFabFocused.value = false
                        }
                    }
            )

            CustomTextField(
                value = formState.surname,
                onValueChange = { viewModel.updateSurname(it) },
                label = stringResource(id = R.string.label_surname),
                isError = formState.surnameError != null,
                nextFocusRequester = emailFocusRequester,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(surnameFocusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            currentFocused.value = "surname"
                            isFabFocused.value = false
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        CustomTextField(
            value = formState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = stringResource(id = R.string.label_email),
            isError = formState.emailError != null,
            nextFocusRequester = passwordFocusRequester,
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        currentFocused.value = "email"
                        isFabFocused.value = false
                    }
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
                        viewModel.register()
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1f)
                    .onFocusChanged {
                        if (it.isFocused) {
                            currentFocused.value = "password"
                            isFabFocused.value = false
                        }
                    }
            )

            CustomFAB(
                modifier = Modifier.padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.registration_FAB_description),
                onClick = {
                    if (formState.isValid) {
                        focusManager.clearFocus()
                        viewModel.register()
                        isFabFocused.value = true
                    }
                },
                containerColor = if (formState.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                contentColor = if (formState.isValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedError = when {
            isFabFocused.value && registrationState is UiState.Error -> (registrationState as UiState.Error).message
            currentFocused.value == "name" -> formState.nameError
            currentFocused.value == "surname" -> formState.surnameError
            currentFocused.value == "email" -> formState.emailError
            currentFocused.value == "password" -> formState.passwordError
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