package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.CustomIcon
import com.unimib.ignitionfinance.presentation.ui.components.dialog.DialogManager
import com.unimib.ignitionfinance.presentation.utils.formatNumberAmerican
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun InputBox(
    inputBoxModel: InputBoxModel,
    isEnabled: Boolean,
    viewModel: SettingsScreenViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    DialogManager(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = { newValue ->
            showDialog = false

            val updatedSettings = state.settings?.let { currentSettings ->
                val updatedSetting = when (inputBoxModel.key) {
                    "monthlyWithdrawalsWithoutPension" -> currentSettings.copy(
                        withdrawals = currentSettings.withdrawals.copy(
                            withoutPension = newValue ?: currentSettings.withdrawals.withoutPension
                        )
                    )
                    "monthlyWithdrawalsWithPension" -> currentSettings.copy(
                        withdrawals = currentSettings.withdrawals.copy(
                            withPension = newValue ?: currentSettings.withdrawals.withPension
                        )
                    )
                    "taxRatePercentage" -> currentSettings.copy(
                        expenses = currentSettings.expenses.copy(
                            taxRatePercentage = newValue ?: currentSettings.expenses.taxRatePercentage
                        )
                    )
                    "stampDutyPercentage" -> currentSettings.copy(
                        expenses = currentSettings.expenses.copy(
                            stampDutyPercentage = newValue ?: currentSettings.expenses.stampDutyPercentage
                        )
                    )
                    "loadPercentage" -> currentSettings.copy(
                        expenses = currentSettings.expenses.copy(
                            loadPercentage = newValue ?: currentSettings.expenses.loadPercentage
                        )
                    )
                    "yearsInFire" -> currentSettings.copy(
                        intervals = currentSettings.intervals.copy(
                            yearsInFIRE = newValue ?: currentSettings.intervals.yearsInFIRE
                        )
                    )
                    "yearsInPaidRetirement" -> currentSettings.copy(
                        intervals = currentSettings.intervals.copy(
                            yearsInPaidRetirement = newValue ?: currentSettings.intervals.yearsInPaidRetirement
                        )
                    )
                    "yearsOfBuffer" -> currentSettings.copy(
                        intervals = currentSettings.intervals.copy(
                            yearsOfBuffer = newValue ?: currentSettings.intervals.yearsOfBuffer
                        )
                    )
                    "numberOfSimulations" -> currentSettings.copy(
                        numberOfSimulations = newValue ?: currentSettings.numberOfSimulations
                    )
                    else -> currentSettings
                }
                updatedSetting
            }

            updatedSettings?.let {
                viewModel.updateSettings(it)
            }
        },
        dialogTitle = stringResource(id = R.string.dialog_update_amount),
        prefix = inputBoxModel.prefix
    )

    val formattedInputValue = remember(inputBoxModel.inputValue.value.text) {
        formatNumberAmerican(inputBoxModel.inputValue.value.text)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = inputBoxModel.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            if (isEnabled) {
                                showDialog = true
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                InputBoxBody(
                    prefix = inputBoxModel.prefix,
                    inputValue = formattedInputValue
                )
            }

            CustomIcon(
                icon = painterResource(id = inputBoxModel.iconResId),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
