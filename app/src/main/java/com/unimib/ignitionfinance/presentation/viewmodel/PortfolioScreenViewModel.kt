package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.remote.model.api.StockData
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.domain.usecase.flag.*
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchExchangeUseCase
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchHistoricalDataUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.cash.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.cash.UpdateUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.AddProductToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.UpdateProductListUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.PortfolioScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.ProductPerformance
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PortfolioScreenViewModel @Inject constructor(
    private val getUserCashUseCase: GetUserCashUseCase,
    private val updateUserCashUseCase: UpdateUserCashUseCase,
    private val getProductListUseCase: GetProductListUseCase,
    private val updateProductListUseCase: UpdateProductListUseCase,
    private val addProductToDatabaseUseCase: AddProductToDatabaseUseCase,
    private val getFirstAddedUseCase: GetFirstAddedUseCase,
    private val updateFirstAddedUseCase: UpdateFirstAddedUseCase,
    private val fetchExchangeUseCase: FetchExchangeUseCase,
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioScreenState())
    val state: StateFlow<PortfolioScreenState> = _state

    init {
        getCash()
        getProducts()
        getFirstAdded()
        fetchExchangeRates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processHistoricalData(): List<ProductPerformance> {
        val historicalDataList: List<Map<String, StockData>> = state.value.historicalData
        val products: List<Product> = state.value.products

        val singleProductHistory = state.value.singleProductHistory
        val singleProductHistoryTicker = state.value.singleProductHistoryTicker

        val performances = mutableListOf<ProductPerformance>()

        val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val purchaseFormatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val purchaseFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val lastUpdateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val currentDateTime = LocalDateTime.now()

        fun parsePurchaseDate(dateStr: String): LocalDate? {
            return try {
                LocalDate.parse(dateStr, purchaseFormatter1)
            } catch (_: Exception) {
                try {
                    LocalDate.parse(dateStr, purchaseFormatter2)
                } catch (_: Exception) {
                    null
                }
            }
        }

        products.forEachIndexed { index, product ->
            val originalPurchaseDateStr = product.purchaseDate
            val purchaseDate = parsePurchaseDate(originalPurchaseDateStr) ?: return@forEachIndexed

            val lastUpdated = LocalDateTime.parse(product.lastUpdated, lastUpdateFormatter)
            val needsUpdate = ChronoUnit.HOURS.between(lastUpdated, currentDateTime) >= 24

            val historicalDataMap: Map<String, StockData>? =
                if (singleProductHistory != null && product.ticker == singleProductHistoryTicker) {
                    singleProductHistory
                } else {
                    historicalDataList.getOrNull(index)
                }

            if (historicalDataMap == null) return@forEachIndexed

            val dateStockList: List<Pair<LocalDate, StockData>> = historicalDataMap.mapNotNull { (dateStr, stockData) ->
                try {
                    val date = LocalDate.parse(dateStr, isoFormatter)
                    Pair(date, stockData)
                } catch (_: Exception) {
                    null
                }
            }
            if (dateStockList.isEmpty()) return@forEachIndexed

            val closestEntry: Pair<LocalDate, StockData> = dateStockList.minByOrNull { (date, _) ->
                abs(ChronoUnit.DAYS.between(date, purchaseDate))
            } ?: return@forEachIndexed

            val lastEntry: Pair<LocalDate, StockData> = dateStockList.maxByOrNull { (date, _) ->
                date
            } ?: return@forEachIndexed

            val purchasePrice: BigDecimal = closestEntry.second.close
            val currentPrice: BigDecimal = lastEntry.second.close

            val percentageChange: BigDecimal = if (purchasePrice.compareTo(BigDecimal.ZERO) != 0) {
                (currentPrice.subtract(purchasePrice))
                    .divide(purchasePrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }

            if (needsUpdate) {
                try {
                    val currentAmount = BigDecimal(product.amount)
                    val dailyPercentageChange = percentageChange.divide(
                        BigDecimal(ChronoUnit.DAYS.between(purchaseDate, lastEntry.first)),
                        4,
                        RoundingMode.HALF_UP
                    )

                    val newAmount = currentAmount.multiply(
                        BigDecimal.ONE.add(
                            dailyPercentageChange.divide(BigDecimal(100), 4, RoundingMode.HALF_UP)
                        )
                    )

                    val updatedProduct = product.copy(
                        amount = newAmount.setScale(2, RoundingMode.HALF_UP).toString(),
                        lastUpdated = currentDateTime.format(lastUpdateFormatter)
                    )

                    viewModelScope.launch {
                        updateProduct(updatedProduct)
                    }
                } catch (_: Exception) {
                }
            }

            val performance = ProductPerformance(
                ticker = product.ticker,
                purchaseDate = closestEntry.first.format(isoFormatter),
                purchasePrice = purchasePrice,
                currentDate = lastEntry.first.format(isoFormatter),
                currentPrice = currentPrice,
                percentageChange = percentageChange,
                currency = product.currency
            )
            performances.add(performance)
        }

        return performances
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProductPerformances() {
        viewModelScope.launch {
            try {
                val performances = processHistoricalData()

                val updatedProducts = state.value.products.map { product ->
                    performances.find { it.ticker == product.ticker }?.let { performance ->
                        product.copy(averagePerformance = performance.percentageChange.toString())
                    } ?: product
                }

                updatedProducts.forEach { product ->
                    updateProduct(product)
                }

                _state.update { currentState ->
                    currentState.copy(
                        products = updatedProducts,
                        productPerformances = performances,
                        productPerformancesState = UiState.Success(performances)
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        productPerformancesState = UiState.Error(
                            e.localizedMessage ?: "Failed to process historical data"
                        )
                    )
                }
            }
        }
    }

    private fun fetchExchangeRates() {
        viewModelScope.launch {
            _state.update { it.copy(
                usdExchangeState = UiState.Loading,
                chfExchangeState = UiState.Loading
            )}

            fetchExchangeUseCase.execute("D.USD.EUR.SP00.A")
                .catch { exception ->
                    _state.update { it.copy(
                        usdExchangeState = UiState.Error(
                            exception.localizedMessage ?: "Failed to fetch USD exchange rate"
                        )
                    )}
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val exchange = result.getOrNull()
                                currentState.copy(
                                    usdExchange = exchange,
                                    usdExchangeState = UiState.Success(exchange)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                usdExchangeState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch USD exchange rate"
                                )
                            )
                            else -> currentState.copy(usdExchangeState = UiState.Idle)
                        }
                    }
                }

            fetchExchangeUseCase.execute("D.CHF.EUR.SP00.A")
                .catch { exception ->
                    _state.update { it.copy(
                        chfExchangeState = UiState.Error(
                            exception.localizedMessage ?: "Failed to fetch CHF exchange rate"
                        )
                    )}
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val exchange = result.getOrNull()
                                currentState.copy(
                                    chfExchange = exchange,
                                    chfExchangeState = UiState.Success(exchange)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                chfExchangeState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch CHF exchange rate"
                                )
                            )
                            else -> currentState.copy(chfExchangeState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun getCash() {
        viewModelScope.launch {
            _state.update { it.copy(cashState = UiState.Loading) }
            getUserCashUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val cash = result.getOrNull()
                                currentState.copy(
                                    cash = cash ?: "0",
                                    cashState = UiState.Success(cash ?: "0")
                                )
                            }
                            result.isFailure -> currentState.copy(
                                cashState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load cash"
                                )
                            )
                            else -> currentState.copy(cashState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun updateCash(newCash: String) {
        viewModelScope.launch {
            _state.update { it.copy(cashState = UiState.Loading) }
            updateUserCashUseCase.execute(newCash)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            cashState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update cash"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val cash = result.getOrNull()
                                currentState.copy(
                                    cash = cash ?: currentState.cash,
                                    cashState = UiState.Success(cash ?: currentState.cash)
                                ).also {
                                    updateFirstAdded()
                                }
                            }
                            result.isFailure -> currentState.copy(
                                cashState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update cash"
                                )
                            )
                            else -> currentState.copy(cashState = UiState.Idle)
                        }
                    }
                }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            _state.update { it.copy(productsState = UiState.Loading) }
            getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY)
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val productList = result.getOrNull()
                                currentState.copy(
                                    products = productList ?: emptyList(),
                                    productsState = UiState.Success(productList ?: emptyList())
                                )
                            }
                            result.isFailure -> currentState.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load products"
                                )
                            )
                            else -> currentState.copy(productsState = UiState.Idle)
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            _state.update { it.copy(productsState = UiState.Loading) }
            addProductToDatabaseUseCase.handleProductStorage(product, BuildConfig.ALPHAVANTAGE_API_KEY)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to handle product storage"
                            )
                        )
                    }
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts()
                        fetchSingleProductHistory(product.ticker)
                    } else {
                        _state.update {
                            it.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to handle product storage"
                                )
                            )
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeProduct(productId: String) {
        viewModelScope.launch {
            val currentProducts = _state.value.products.toMutableList()
            val newProducts = currentProducts.filter { it.ticker != productId }
            _state.update {
                it.copy(
                    products = newProducts,
                    productsState = UiState.Loading
                )
            }

            updateProductListUseCase.removeProduct(productId)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to remove product"
                            )
                        )
                    }
                }
                .collect { result ->
                    result.exceptionOrNull()?.let { error ->
                        _state.update { currentState ->
                            currentState.copy(
                                productsState = UiState.Error(
                                    error.localizedMessage ?: "Failed to remove product"
                                )
                            )
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProduct(updatedProduct: Product) {
        viewModelScope.launch {
            val currentProducts = _state.value.products.toMutableList()
            val index = currentProducts.indexOfFirst { it.ticker == updatedProduct.ticker }
            if (index != -1) {
                currentProducts[index] = updatedProduct
                _state.update { it.copy(products = currentProducts) }
            }

            updateProductListUseCase.updateProduct(updatedProduct)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update product"
                            )
                        )
                    }
                }
                .collect { result ->
                    if (result.isFailure) {
                        getProducts()
                        updateProductPerformances()
                    } else {
                        _state.update {
                            it.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update product"
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun getFirstAdded() {
        viewModelScope.launch {
            _state.update { it.copy(firstAddedState = UiState.Loading) }
            getFirstAddedUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val firstAdded = result.getOrNull()
                                currentState.copy(
                                    isFirstAdded = firstAdded == true,
                                    firstAddedState = UiState.Success(firstAdded == true)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                firstAddedState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load firstAdded"
                                )
                            )
                            else -> currentState.copy(firstAddedState = UiState.Idle)
                        }
                    }
                }
        }
    }

    private fun updateFirstAdded() {
        viewModelScope.launch {
            _state.update { it.copy(firstAddedState = UiState.Loading) }
            updateFirstAddedUseCase.execute(true)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            firstAddedState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update firstAdded"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val firstAdded = result.getOrNull()
                                currentState.copy(
                                    isFirstAdded = firstAdded ?: currentState.isFirstAdded,
                                    firstAddedState = UiState.Success(
                                        firstAdded ?: currentState.isFirstAdded
                                    )
                                )
                            }
                            result.isFailure -> currentState.copy(
                                firstAddedState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update firstAdded"
                                )
                            )
                            else -> currentState.copy(firstAddedState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun calculateUsdAmount(euroAmount: String): String {
        return try {
            val amount = euroAmount.toDoubleOrNull() ?: 0.0
            val exchangeRate = _state.value.usdExchange?.exchangeRate ?: 1.0
            String.format(Locale.US, "%.2f", amount * exchangeRate)
        } catch (_: Exception) {
            "0.00"
        }
    }

    fun calculateChfAmount(euroAmount: String): String {
        return try {
            val amount = euroAmount.toDoubleOrNull() ?: 0.0
            val exchangeRate = _state.value.chfExchange?.exchangeRate ?: 1.0
            String.format(Locale.US, "%.2f", amount * exchangeRate)
        } catch (_: Exception) {
            "0.00"
        }
    }

    fun fetchHistoricalData(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(historicalDataState = UiState.Loading) }
            fetchHistoricalDataUseCase.execute(apiKey)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            historicalDataState = UiState.Error(
                                exception.localizedMessage ?: "Failed to fetch historical data"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val historicalData = result.getOrNull()
                                currentState.copy(
                                    historicalData = historicalData ?: emptyList(),
                                    historicalDataState = UiState.Success(historicalData ?: emptyList())
                                ).also {
                                    updateProductPerformances()
                                }
                            }
                            result.isFailure -> currentState.copy(
                                historicalDataState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch historical data"
                                )
                            )
                            else -> currentState.copy(historicalDataState = UiState.Idle)
                        }
                    }
                }
        }
    }

    private fun fetchSingleProductHistory(ticker: String, symbol: String = "", onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.update { it.copy(singleProductHistoryState = UiState.Loading) }

            fetchHistoricalDataUseCase.fetchSingleProductHistory(ticker, symbol, BuildConfig.ALPHAVANTAGE_API_KEY)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            singleProductHistoryState = UiState.Error(
                                exception.localizedMessage ?: "Failed to fetch product history"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val historicalData = result.getOrNull()
                                val newState = currentState.copy(
                                    singleProductHistoryTicker = ticker,
                                    singleProductHistory = historicalData,
                                    singleProductHistoryState = UiState.Success(historicalData)
                                )
                                onSuccess?.invoke()
                                newState
                            }
                            result.isFailure -> currentState.copy(
                                singleProductHistoryState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch product history"
                                )
                            )
                            else -> currentState.copy(singleProductHistoryState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun toggleCardExpansion(index: Int) {
        _state.update {
            it.copy(
                expandedCardIndex = if (it.expandedCardIndex == index) -1 else index
            )
        }
    }
}