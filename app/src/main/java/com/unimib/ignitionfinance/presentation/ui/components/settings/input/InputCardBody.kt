package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun InputCardBody(
    inputBoxModelList: List<InputBoxModel>,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    inputBoxModelList.forEachIndexed { index, inputBoxData ->
        InputBox(inputBoxModel = inputBoxData, isEnabled = true, viewModel = viewModel)
        if (index < inputBoxModelList.size - 1) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
