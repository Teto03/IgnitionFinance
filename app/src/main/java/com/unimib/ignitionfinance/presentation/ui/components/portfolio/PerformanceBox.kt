package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

@Composable
fun PerformanceBox(
    leftAmount: String,
    rightAmount: String,
    modifier: Modifier = Modifier,
    leftCurrencySymbol: String = "€",
    rightCurrencySymbol: String? = "€",
    leftLabel: String? = null,
    rightLabel: String? = null,
    showDivider: Boolean = true,
    onDeleteClicked: (() -> Unit)? = null,
    percentageChange: String? = null
) {
    if (showDivider) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (showDivider) 8.dp else 0.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AmountBox(
                amount = leftAmount,
                currencyCode = leftCurrencySymbol,
                alignRight = false,
                bottomLabel = leftLabel,
                isReadOnly = true
            )
        }

        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (onDeleteClicked != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = percentageChange ?: "----",
                        style = TypographyMedium.titleLarge,
                        color = if (percentageChange?.startsWith("-") == true) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%",
                        style = TypographyMedium.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                IconButton(
                    onClick = onDeleteClicked,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_delete_24),
                        contentDescription = stringResource(id = R.string.delete_product),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            AmountBox(
                amount = rightAmount,
                currencyCode = rightCurrencySymbol ?: leftCurrencySymbol,
                alignRight = true,
                bottomLabel = rightLabel,
                isReadOnly = true
            )
        }
    }
}