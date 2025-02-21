package com.unimib.ignitionfinance.presentation.ui.components.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.theme.PrimaryBlack
import com.unimib.ignitionfinance.presentation.ui.theme.PrimaryWhite

@Composable
fun IntroImage(
    textVisible: Boolean,
    isFabClickable: Boolean,
    onNavigate: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val videoHeight = screenHeight * 0.8f
    val introText = stringResource(id = R.string.intro_description)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(videoHeight)
            .background(
                MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        BackgroundImage()

        AnimatedText(text = introText, visible = textVisible)

        CustomFAB(
            onClick = {
                if (isFabClickable) {
                    onNavigate()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            containerColor = PrimaryWhite,
            contentColor = PrimaryBlack,
            icon = painterResource(id = R.drawable.outline_keyboard_arrow_up_24),
            contentDescription = stringResource(id = R.string.swipe_up_FAB_description)
        )
    }
}