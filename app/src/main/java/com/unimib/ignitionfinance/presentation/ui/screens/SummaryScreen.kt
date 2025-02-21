package com.unimib.ignitionfinance.presentation.ui.screens

import com.unimib.ignitionfinance.presentation.ui.components.navigation.BottomNavigationBarInstance
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.summary.AssetAllocationCard
import com.unimib.ignitionfinance.presentation.ui.components.summary.NetWorthDisplay
import com.unimib.ignitionfinance.presentation.ui.components.summary.PerformanceCard
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.utils.calculatePerformanceMetrics
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SummaryScreen(
    navController: NavController,
    portfolioViewModel: PortfolioScreenViewModel = hiltViewModel(),
    summaryViewModel: SummaryScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val portfolioState = portfolioViewModel.state.collectAsState().value
    val summaryState = summaryViewModel.state.collectAsState().value
    val isNetWorthHidden = summaryState.isNetWorthHidden

    val cash = remember { mutableDoubleStateOf(0.0) }
    val isLoading = remember { mutableStateOf(true) }
    val valuesCalculated = remember { mutableStateOf(false) }
    var showAssetCard by remember { mutableStateOf(false) }
    var showPerformanceCard by remember { mutableStateOf(false) }

    val performanceMetrics = remember(portfolioState.products) {
        calculatePerformanceMetrics(portfolioState.products)
    }

    LaunchedEffect(Unit) {
        portfolioViewModel.getCash()
        summaryViewModel.getInvested()
    }

    LaunchedEffect(portfolioState.cashState, summaryState.investedState) {
        isLoading.value = when {
            portfolioState.cashState is UiState.Loading ||
                    summaryState.investedState is UiState.Loading -> true

            portfolioState.cashState is UiState.Success &&
                    summaryState.investedState is UiState.Success -> {
                val cashString = portfolioState.cashState.data
                val cleanCashString = cashString.replace("[^0-9.]".toRegex(), "")
                cash.doubleValue = cleanCashString.toDoubleOrNull() ?: 0.0

                summaryViewModel.calculateNetWorth(cash.doubleValue)

                valuesCalculated.value = true
                false
            }
            else -> false
        }
    }

    LaunchedEffect(valuesCalculated.value) {
        if (valuesCalculated.value) {
            delay(300)
            showAssetCard = true
            delay(200)
            showPerformanceCard = true
        }
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.summary_title))
        },
        bottomBar = {
            BottomNavigationBarInstance(navController = navController)
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                item {
                    NetWorthDisplay(
                        inputBoxModel = InputBoxModel(
                            label = "Your net worth is:",
                            prefix = "€",
                            inputValue = remember { mutableStateOf(TextFieldValue("")) },
                            key = "NetWorth",
                            iconResId = R.drawable.outline_person_apron_24
                        ),
                        netWorth = summaryState.netWorth,
                        isLoading = isLoading.value || summaryState.netWorthState is UiState.Loading,
                        isNetWorthHidden = isNetWorthHidden,
                        onVisibilityToggle = { summaryViewModel.toggleNetWorthVisibility() }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    AnimatedVisibility(
                        visible = showAssetCard,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = 0
                            )
                        )
                    ) {
                        AssetAllocationCard(
                            cash = cash.doubleValue,
                            invested = summaryState.invested
                        )
                    }

                    AnimatedVisibility(
                        visible = showPerformanceCard,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = 200
                            )
                        )
                    ) {
                        performanceMetrics?.let { (avgPerformance, bestPerformer, worstPerformer) ->
                            PerformanceCard(
                                averagePerformance = avgPerformance,
                                bestPerformer = bestPerformer,
                                worstPerformer = worstPerformer
                            )
                        }
                    }
                }
            }
        }
    )
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)