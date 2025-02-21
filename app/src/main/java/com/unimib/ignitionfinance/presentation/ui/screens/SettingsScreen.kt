package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputCard
import com.unimib.ignitionfinance.presentation.ui.components.settings.select.SelectCard
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleSettings
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val state by settingsViewModel.state.collectAsState()
    val listState = rememberLazyListState()

    var selectedCardIndex by remember { mutableIntStateOf(-1) }

    val settingsData = state.settings ?: settingsViewModel.getDefaultSettings()

    var inflationModel by remember {
        mutableStateOf(
            SelectBoxModel(
                key = "inflationModel",
                text = "Choose the inflation model:",
                displayedTexts = listOf("NORMAL", "SCALE", "LOGNORMAL"),
                selectedText = settingsData.inflationModel
            )
        )
    }

    LaunchedEffect(selectedCardIndex) {
        if (selectedCardIndex != -1) {
            listState.animateScrollToItem(selectedCardIndex)
        }
    }

    LaunchedEffect(Unit) {
        settingsViewModel.getUserSettings()
    }

    LaunchedEffect(state.settings) {
        state.settings?.let {
            inflationModel = inflationModel.copy(selectedText = it.inflationModel)
        }
    }

    Scaffold(
        topBar = {
            TitleSettings(
                title = stringResource(id = R.string.settings_title),
                navController = navController
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = listState
            ) {
                item {
                    InputCard(
                        label = stringResource(id = R.string.withdraw_label),
                        title = stringResource(id = R.string.withdraw_title),
                        inputBoxModelList = listOf(
                            InputBoxModel(
                                key = "monthlyWithdrawalsWithoutPension",
                                label = stringResource(id = R.string.monthly_withdrawals_no_pension),
                                prefix = stringResource(id = R.string.currency_eur),
                                iconResId = R.drawable.outline_person_apron_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.withdrawals.withoutPension))
                                }
                            ),
                            InputBoxModel(
                                key = "monthlyWithdrawalsWithPension",
                                label = stringResource(id = R.string.monthly_withdrawals_with_pension),
                                prefix = stringResource(id = R.string.currency_eur),
                                iconResId = R.drawable.outline_person_4_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.withdrawals.withPension))
                                }
                            )
                        ),
                        isExpanded = state.expandedCardIndex == 0,
                        onCardClicked = {
                            settingsViewModel.toggleCardExpansion(0)
                            selectedCardIndex = 0
                        }
                    )
                }
                item {
                    SelectCard(
                        label = stringResource(id = R.string.inflation_select_label),
                        title = stringResource(id = R.string.inflation_title),
                        model = inflationModel,
                        isExpanded = state.expandedCardIndex == 1,
                        onCardClicked = {
                            settingsViewModel.toggleCardExpansion(1)
                            selectedCardIndex = 0
                        },
                        onTextSelected = { selectedText ->
                            inflationModel = inflationModel.copy(selectedText = selectedText)
                            state.settings?.let { currentSettings ->
                                val updatedSettings = currentSettings.copy(
                                    inflationModel = selectedText
                                )
                                settingsViewModel.updateSettings(updatedSettings)
                            }
                        }
                    )
                }
                item {
                    InputCard(
                        label = stringResource(id = R.string.expenses_label),
                        title = stringResource(id = R.string.expenses_title),
                        inputBoxModelList = listOf(
                            InputBoxModel(
                                key = "taxRatePercentage",
                                label = stringResource(id = R.string.tax_rate_percentage),
                                prefix = stringResource(id = R.string.percentage_prefix),
                                iconResId = R.drawable.outline_account_balance_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.expenses.taxRatePercentage))
                                }
                            ),
                            InputBoxModel(
                                key = "stampDutyPercentage",
                                label = stringResource(id = R.string.stamp_duty_percentage),
                                prefix = stringResource(id = R.string.percentage_prefix),
                                iconResId = R.drawable.outline_position_top_right_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.expenses.stampDutyPercentage))
                                }
                            ),
                            InputBoxModel(
                                key = "loadPercentage",
                                label = stringResource(id = R.string.load_percentage),
                                prefix = stringResource(id = R.string.percentage_prefix),
                                iconResId = R.drawable.outline_weight_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.expenses.loadPercentage))
                                }
                            )
                        ),
                        isExpanded = state.expandedCardIndex == 2,
                        onCardClicked = {
                            settingsViewModel.toggleCardExpansion(2)
                            selectedCardIndex = 1
                        }
                    )
                }
                item {
                    InputCard(
                        label = stringResource(id = R.string.intervals_label),
                        title = stringResource(id = R.string.intervals_title),
                        inputBoxModelList = listOf(
                            InputBoxModel(
                                key = "yearsInPaidRetirement",
                                label = stringResource(id = R.string.target_years),
                                prefix = stringResource(id = R.string.years_prefix),
                                iconResId = R.drawable.outline_send_money_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.intervals.yearsInPaidRetirement))
                                }
                            ),
                            InputBoxModel(
                                key = "yearsInFire",
                                label = stringResource(id = R.string.years_in_fire),
                                prefix = stringResource(id = R.string.years_prefix),
                                iconResId = R.drawable.outline_local_fire_department_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.intervals.yearsInFIRE))
                                }
                            ),
                            InputBoxModel(
                                key = "yearsOfBuffer",
                                label = stringResource(id = R.string.years_of_buffer),
                                prefix = stringResource(id = R.string.years_prefix),
                                iconResId = R.drawable.outline_clock_loader_10_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.intervals.yearsOfBuffer))
                                }
                            )
                        ),
                        isExpanded = state.expandedCardIndex == 3,
                        onCardClicked = {
                            settingsViewModel.toggleCardExpansion(3)
                            selectedCardIndex = 2
                        }
                    )
                }
                item {
                    InputCard(
                        label = stringResource(id = R.string.simulations_label),
                        title = stringResource(id = R.string.simulations_title),
                        inputBoxModelList = listOf(
                            InputBoxModel(
                                key = "numberOfSimulations",
                                label = stringResource(id = R.string.number_of_simulations),
                                prefix = stringResource(id = R.string.number_prefix),
                                iconResId = R.drawable.outline_autoplay_24,
                                inputValue = remember(state.settings) {
                                    mutableStateOf(TextFieldValue(settingsData.numberOfSimulations))
                                }
                            )
                        ),
                        isExpanded = state.expandedCardIndex == 4,
                        onCardClicked = {
                            settingsViewModel.toggleCardExpansion(4)
                            selectedCardIndex = 3
                        }
                    )
                }
            }
        }
    )
}
