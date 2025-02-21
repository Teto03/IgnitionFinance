package com.unimib.ignitionfinance.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.intro.IntroImage
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.viewmodel.IntroScreenViewModel

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun IntroScreen(navController: NavController, introScreenViewModel: IntroScreenViewModel = viewModel()) {
    val initialOffset = 1000f
    val targetOffset = 24f
    val animatedOffset = remember { Animatable(initialOffset) }

    LaunchedEffect(Unit) {
        animatedOffset.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.app_title))
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = animatedOffset.value.dp)
                ) {
                    IntroImage(
                        textVisible = introScreenViewModel.textVisible.value,
                        isFabClickable = introScreenViewModel.isFabClickable.value,
                        onNavigate = {
                            navController.navigate(Destinations.LoginScreen.route) {
                                popUpTo(Destinations.IntroScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    )
}