package com.unimib.ignitionfinance.presentation.ui.components.navigation

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unimib.ignitionfinance.presentation.model.BottomNavigationItemModel
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyBold

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    items: List<BottomNavigationItemModel>,
    navController: NavController
) {
    val hapticFeedback = LocalHapticFeedback.current
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    val navigationState = rememberNavigationState(initialDestination = Destinations.PortfolioScreen.route)

    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        items.forEach { item ->
            val isSelected = currentDestination == item.destination
            val animatedImageVector = AnimatedImageVector.animatedVectorResource(id = item.iconRes)
            var atEnd by remember { mutableStateOf(isSelected) }

            LaunchedEffect(isSelected) {
                if (isSelected) {
                    atEnd = true
                    navigationState.updatePreviousDestination(currentDestination)
                }
            }

            NavigationBarItem(
                icon = {
                    val painter = rememberAnimatedVectorPainter(animatedImageVector, atEnd)
                    Icon(
                        painter = painter,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = if (isSelected) TypographyBold.bodySmall else TypographyMedium.bodySmall
                    )
                },
                selected = isSelected,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (!isSelected) {
                        navController.navigate(item.destination) {
                            popUpTo(navigationState.previousDestination) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}