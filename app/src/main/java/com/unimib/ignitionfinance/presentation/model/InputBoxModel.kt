package com.unimib.ignitionfinance.presentation.model

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

data class InputBoxModel (
    val key: String,
    val label: String,
    val prefix: String = "€",
    val iconResId: Int = 0,
    val inputValue: MutableState<TextFieldValue>
)