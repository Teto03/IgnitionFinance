package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputBoxBody
import java.util.Locale

@Composable
fun NetWorthDisplay(
    inputBoxModel: InputBoxModel,
    netWorth: Double? = 0.0,
    isLoading: Boolean = false,
    isNetWorthHidden: Boolean = false,
    showVisibilityIcon: Boolean = true,
    onVisibilityToggle: () -> Unit = { }
) {
    val formattedNetWorth = remember(netWorth) {
        if (netWorth?.rem(1) == 0.0) {
            String.format(Locale.US, "%,.0f", netWorth)
        } else {
            String.format(Locale.US, "%,.2f", netWorth)
        }
    }

    val displayValue = if (isNetWorthHidden) "----" else formattedNetWorth

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = inputBoxModel.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    InputBoxBody(
                        prefix = inputBoxModel.prefix,
                        inputValue = "Loading ...",
                        padding = 4.dp,
                        textStyle = MaterialTheme.typography.displaySmall,
                    )
                } else {
                    InputBoxBody(
                        prefix = inputBoxModel.prefix,
                        inputValue = displayValue,
                        padding = 4.dp,
                        textStyle = MaterialTheme.typography.displaySmall,
                        animated = true
                    )
                }
            }

            if (showVisibilityIcon) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { onVisibilityToggle() }
                ) {
                    IconButton(
                        onClick = onVisibilityToggle
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isNetWorthHidden) {
                                    R.drawable.outline_visibility_24
                                } else {
                                    R.drawable.outline_visibility_off_24
                                }
                            ),
                            contentDescription = stringResource(id = R.string.go_back_FAB_description),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}