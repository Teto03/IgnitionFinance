package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class IntroScreenViewModel : ViewModel() {

    val textVisible = mutableStateOf(false)
    val isFabClickable = mutableStateOf(false)

    init {
        startIntroAnimation()
    }

    private fun startIntroAnimation() {
        viewModelScope.launch {
            delay(1000)
            textVisible.value = true
            delay(500)
            isFabClickable.value = true
        }
    }
}
