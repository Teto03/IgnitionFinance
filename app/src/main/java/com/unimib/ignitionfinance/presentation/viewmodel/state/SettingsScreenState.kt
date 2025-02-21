package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.data.remote.model.user.Settings

data class SettingsScreenState(
    val settings: Settings? = null,
    val settingsState: UiState<Settings> = UiState.Idle,
    val expandedCardIndex: Int = -1
)
