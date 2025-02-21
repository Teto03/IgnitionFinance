package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.simulation.GetLastSimulationResultUseCase
import com.unimib.ignitionfinance.domain.usecase.simulation.StartSimulationUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.SimulationScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimulationScreenViewModel @Inject constructor(
    private val startSimulationUseCase: StartSimulationUseCase,
    private val getLastSimulationResultUseCase: GetLastSimulationResultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state.asStateFlow()

    private val _validateSettings = MutableStateFlow<String?>(null)
    val validateSettings: StateFlow<String?> = _validateSettings.asStateFlow()

    init {
        getLastSimulation()
    }

    private fun getLastSimulation() {
        viewModelScope.launch {
            try {
                getLastSimulationResultUseCase.execute()
                    .collect { result ->
                        result.fold(
                            onSuccess = { simulationResult ->
                                _state.update { currentState ->
                                    currentState.copy(
                                        lastSimulationResult = simulationResult,
                                        simulationState = UiState.Idle
                                    )
                                }
                            },
                            onFailure = { error ->
                                if (error is IllegalArgumentException) {
                                    _validateSettings.value = error.message
                                }
                                _state.update { currentState ->
                                    currentState.copy(
                                        simulationState = UiState.Error(
                                            error.localizedMessage ?: "Error loading last simulation"
                                        )
                                    )
                                }
                            }
                        )
                    }
            } catch (e: Exception) {
                if (e is IllegalArgumentException) {
                    _validateSettings.value = e.message
                }
                _state.update {
                    it.copy(
                        simulationState = UiState.Error(
                            e.localizedMessage ?: "Failed to load last simulation"
                        )
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation() {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }

            try {
                startSimulationUseCase.execute()
                    .collect { result ->
                        result.fold(
                            onSuccess = { data ->
                                _state.update { currentState ->
                                    currentState.copy(
                                        simulationState = UiState.Success(data),
                                        lastSimulationResult = data
                                    )
                                }
                            },
                            onFailure = { error ->
                                if (error is IllegalArgumentException) {
                                    _validateSettings.value = error.message
                                }
                                _state.update { currentState ->
                                    currentState.copy(
                                        simulationState = UiState.Error(
                                            error.localizedMessage ?: "Simulation error"
                                        )
                                    )
                                }
                            }
                        )
                    }
            } catch (e: Exception) {
                if (e is IllegalArgumentException) {
                    _validateSettings.value = e.message
                }
                _state.update {
                    it.copy(
                        simulationState = UiState.Error(
                            e.localizedMessage ?: "Simulation failed"
                        )
                    )
                }
            }
        }
    }

    fun clearValidationError() {
        _validateSettings.value = null
    }
}