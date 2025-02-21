package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputCardHeader
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale
import com.unimib.ignitionfinance.presentation.viewmodel.state.ProductPerformance

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardCard(
    modifier: Modifier,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    viewModel: PortfolioScreenViewModel = viewModel(),
    isin: String,
    ticker: String,
    isCash: Boolean = false,
    product: Product? = null,
    performance: ProductPerformance? = null
) {
    val state = viewModel.state.collectAsState()

    val cardHeight = animateDpAsState(
        targetValue = when {
            isCash && isExpanded -> 336.dp
            isCash && !isExpanded -> 160.dp
            !isCash && isExpanded -> 360.dp
            else -> 160.dp
        }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight.value)
            .clickable(
                onClick = onCardClicked,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputCardHeader(
                    label = isin,
                    title = ticker,
                    isExpanded = isExpanded,
                    onCardClicked = onCardClicked,
                    titleFontSize = MaterialTheme.typography.displayLarge.fontSize
                )

                if (isExpanded) {
                    if (isCash) {
                        AmountBox(
                            amount = state.value.cash,
                            onAmountChanged = { newAmount ->
                                newAmount?.let { viewModel.updateCash(it) }
                            },
                            currencyCode = "EUR",
                            modifier = modifier.padding(bottom = 36.dp)
                        )

                        when {
                            state.value.usdExchangeState is UiState.Loading ||
                                    state.value.chfExchangeState is UiState.Loading -> {
                                PerformanceBox(
                                    leftAmount = stringResource(id = R.string.no_data),
                                    rightAmount = stringResource(id = R.string.no_data),
                                    leftCurrencySymbol = stringResource(id = R.string.currency_usd),
                                    rightCurrencySymbol = stringResource(id = R.string.currency_chf),
                                    leftLabel = stringResource(id = R.string.label_usd),
                                    rightLabel = stringResource(id = R.string.label_chf),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            state.value.usdExchangeState is UiState.Error ||
                                    state.value.chfExchangeState is UiState.Error -> {
                                PerformanceBox(
                                    leftAmount = stringResource(id = R.string.no_data),
                                    rightAmount = stringResource(id = R.string.no_data),
                                    leftCurrencySymbol = stringResource(id = R.string.currency_usd),
                                    rightCurrencySymbol = stringResource(id = R.string.currency_chf),
                                    leftLabel = stringResource(id = R.string.label_usd),
                                    rightLabel = stringResource(id = R.string.label_chf),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> {
                                PerformanceBox(
                                    leftAmount = viewModel.calculateUsdAmount(state.value.cash),
                                    rightAmount = viewModel.calculateChfAmount(state.value.cash),
                                    leftCurrencySymbol = stringResource(id = R.string.currency_usd),
                                    rightCurrencySymbol = stringResource(id = R.string.currency_chf),
                                    leftLabel = stringResource(id = R.string.label_usd),
                                    rightLabel = stringResource(id = R.string.label_chf),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        if (product != null) {
                            AmountBox(
                                amount = product.amount,
                                onAmountChanged = { newAmount ->
                                    newAmount?.let {
                                        val updatedProduct = product.copy(amount = it)
                                        viewModel.updateProduct(updatedProduct)
                                    }
                                },
                                currencyCode = "EUR",
                                modifier = modifier.padding(bottom = 36.dp),
                                isProduct = true
                            )

                            val perf = performance ?: state.value.productPerformances.find { it.ticker == product.ticker }

                            if (perf != null) {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                                val formattedPurchaseDate = try {
                                    val date = inputFormat.parse(perf.purchaseDate)
                                    date?.let { outputFormat.format(it) } ?: "Invalid Date"
                                } catch (_: Exception) {
                                    "Invalid Date"
                                }

                                val formattedCurrentDate = try {
                                    val date = inputFormat.parse(perf.currentDate)
                                    date?.let { outputFormat.format(it) } ?: "Invalid Date"
                                } catch (_: Exception) {
                                    "Invalid Date"
                                }

                                val percentageValue = perf.percentageChange.setScale(2, RoundingMode.HALF_UP)
                                val sign = if (percentageValue >= BigDecimal.ZERO) "+" else ""
                                val formattedPercentage = "$sign${percentageValue.toPlainString()}"

                                PerformanceBox(
                                    leftAmount = perf.purchasePrice.toString(),
                                    rightAmount = perf.currentPrice.toString(),
                                    leftCurrencySymbol = perf.currency,
                                    rightCurrencySymbol = perf.currency,
                                    leftLabel = formattedPurchaseDate,
                                    rightLabel = formattedCurrentDate,
                                    percentageChange = formattedPercentage,
                                    modifier = Modifier.fillMaxWidth(),
                                    onDeleteClicked = { viewModel.removeProduct(product.ticker) }
                                )
                            } else {
                                PerformanceBox(
                                    leftAmount = stringResource(id = R.string.no_data),
                                    rightAmount = stringResource(id = R.string.no_data),
                                    leftLabel = stringResource(id = R.string.label_historical),
                                    rightLabel = stringResource(id = R.string.label_no_current_data),
                                    modifier = Modifier.fillMaxWidth(),
                                    onDeleteClicked = { viewModel.removeProduct(product.ticker) },
                                    percentageChange = if (product.averagePerformance.isNotEmpty()) {
                                        val perfValue = product.averagePerformance.toBigDecimalOrNull()
                                        if (perfValue != null) {
                                            val sign = if (perfValue >= BigDecimal.ZERO) "+" else ""
                                            "$sign${perfValue.setScale(2, RoundingMode.HALF_UP)}"
                                        } else null
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}