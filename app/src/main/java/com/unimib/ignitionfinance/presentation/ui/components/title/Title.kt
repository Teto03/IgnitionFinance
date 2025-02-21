package com.unimib.ignitionfinance.presentation.ui.components.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB

@Composable
fun Title(
    title: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    TitleBox(modifier) {
        TitleText(
            text = title,
            color = color,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
fun TitleWithDescription(
    title: String,
    description: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    TitleBox(modifier) {
        TitleText(
            text = title,
            color = color,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        DescriptionText(
            text = description,
            color = color,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
fun TitleWithButton(
    title: String,
    description: String,
    navController: NavController,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    TitleBox(modifier) {
        TitleText(
            text = title,
            color = color,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        DescriptionText(
            text = description,
            color = color,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        CustomFAB(
            onClick = { navController.navigate(Destinations.SettingsScreen.route) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = painterResource(id = R.drawable.outline_settings_24),
            contentDescription = stringResource(id = R.string.settings_FAB_description),
            fabSize = 40.dp,
            iconSize = 24.dp,
            hapticFeedbackType = HapticFeedbackType.TextHandleMove
        )
    }
}

@Composable
fun TitleSettings(
    title: String,
    navController: NavController,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    TitleBox(
        modifier = modifier.height(256.dp),
        contentAlignment = Alignment.Center
    ) {
        val hapticFeedback = LocalHapticFeedback.current

        IconButton(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                contentDescription = stringResource(id = R.string.go_back_FAB_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        TitleText(
            text = title,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 64.dp)
        )
    }
}

@Composable
private fun TitleBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

