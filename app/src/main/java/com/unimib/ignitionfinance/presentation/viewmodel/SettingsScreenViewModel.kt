package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.SetDefaultSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.UpdateUserSettingsUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.unimib.ignitionfinance.presentation.viewmodel.state.SettingsScreenState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val setDefaultSettingsUseCase: SetDefaultSettingsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state

    fun getDefaultSettings(): Settings {
        return setDefaultSettingsUseCase.execute()
    }

    fun getUserSettings() {
        viewModelScope.launch {
            _state.update { it.copy(settingsState = UiState.Loading) }
            getUserSettingsUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val settings = result.getOrNull()
                                if (settings != null) {
                                    currentState.copy(
                                        settings = settings,
                                        settingsState = UiState.Success(settings)
                                    )
                                } else {
                                    currentState.copy(
                                        settingsState = UiState.Error("Settings not found")
                                    )
                                }
                            }
                            result.isFailure -> currentState.copy(
                                settingsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load settings"
                                )
                            )
                            else -> currentState.copy(settingsState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun updateSettings(newSettings: Settings) {
        viewModelScope.launch {
            _state.update { it.copy(settingsState = UiState.Loading) }
            updateUserSettingsUseCase.execute(newSettings)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            settingsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update settings"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val settings = result.getOrNull()
                                if (settings != null) {
                                    currentState.copy(
                                        settings = settings,
                                        settingsState = UiState.Success(settings)
                                    )
                                } else {
                                    currentState.copy(
                                        settingsState = UiState.Error("Failed to update settings")
                                    )
                                }
                            }
                            result.isFailure -> currentState.copy(
                                settingsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update settings"
                                )
                            )
                            else -> currentState.copy(settingsState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun toggleCardExpansion(cardIndex: Int) {
        _state.update {
            it.copy(
                expandedCardIndex = if (it.expandedCardIndex == cardIndex) -1 else cardIndex
            )
        }
    }
}