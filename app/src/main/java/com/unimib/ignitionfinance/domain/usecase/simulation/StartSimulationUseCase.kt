package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.domain.simulation.AnnualReturnsMatrixGenerator
import com.unimib.ignitionfinance.domain.simulation.FireSimulator
import com.unimib.ignitionfinance.domain.simulation.InflationModel
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import com.unimib.ignitionfinance.domain.simulation.SimulationConfigFactory
import com.unimib.ignitionfinance.domain.simulation.WithdrawalCalculator
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.validation.SimulationConfigValidator
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartSimulationUseCase @Inject constructor(
    private val buildDatasetUseCase: BuildDatasetUseCase,
    private val getUserDatasetUseCase: GetUserDatasetUseCase,
    private val networkUtils: NetworkUtils,
    private val configFactory: SimulationConfigFactory,
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
) {
    companion object {
        private const val SUCCESS_RATE_THRESHOLD = 0.95
        private const val CAPITAL_TOLERANCE = 100.0
        private const val MAX_ITERATIONS = 20
        private const val GOLDEN_RATIO = 1.618033988749895
        private const val PARALLEL_POINTS = 5
    }

    private suspend fun getCurrentUserId(): String? {
        return authRepository.getCurrentUser()
            .firstOrNull()
            ?.getOrNull()
            ?.id
            ?.takeIf { it.isNotEmpty() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(): Flow<Result<Pair<List<SimulationResult>, Double>>> = flow {
        try {
            val userId = getCurrentUserId()
                ?: throw IllegalStateException("Failed to get current user ID")
            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            val existingDataset = currentUser.dataset

            val isNetworkAvailable = networkUtils.isNetworkAvailable()

            val datasetResult = if (isNetworkAvailable) {
                buildDatasetUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            } else {
                getUserDatasetUseCase.execute().first()
            }

            val newDataset = datasetResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val configResult = configFactory.createConfig().first()
            val baseConfig = configResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }.copy(
                dataset = newDataset.ifEmpty { existingDataset }
            )
            val validationErrors = SimulationConfigValidator.validate(baseConfig) // Validate only the base config now
            if (validationErrors.isNotEmpty()) {
                val errorMessage = validationErrors.joinToString("\n")
                emit(Result.failure(IllegalArgumentException(errorMessage)))
                return@flow
            }
            val capitalIncrements = listOf(0.0, 50_000.0, 100_000.0, 150_000.0)
            val configs = capitalIncrements.map { increment ->
                baseConfig.copy(
                    capital = baseConfig.capital.copy(
                        cash = baseConfig.capital.cash + increment
                    )
                )
            }

            val simulationResults = coroutineScope {
                configs.map { config ->
                    async(Dispatchers.Default) { runSimulation(config) }
                }.awaitAll()
            }

            val baseTotal = baseConfig.capital.total
            val simulationPoints = simulationResults.zip(capitalIncrements).map { (result, increment) ->
                (baseTotal + increment) to result.successRate
            }

            var lowerBound: Double? = null
            var upperBound: Double? = null

            if (simulationPoints.first().second >= SUCCESS_RATE_THRESHOLD) {
                lowerBound = 0.0
                upperBound = simulationPoints.first().first
            } else {
                for (i in 0 until simulationPoints.size - 1) {
                    val (cap1, rate1) = simulationPoints[i]
                    val (cap2, rate2) = simulationPoints[i + 1]
                    if (rate1 < SUCCESS_RATE_THRESHOLD && rate2 >= SUCCESS_RATE_THRESHOLD) {
                        lowerBound = cap1
                        upperBound = cap2
                        break
                    }
                }
            }

            if (lowerBound == null) {
                val lastCapital = simulationPoints.last().first
                lowerBound = lastCapital
                upperBound = lastCapital * GOLDEN_RATIO
            }


            val fuckYouMoney = calculateOptimizedFuckYouMoneyWithBracket(
                baseConfig,
                lowerBound,
                upperBound ?: Double.MAX_VALUE
            )

            val simulationOutcome = Pair(simulationResults, fuckYouMoney)
            val simulationOutcomeJson = Gson().toJson(simulationOutcome)

            localDatabaseRepository.updateSimulationOutcome(
                userId,
                outcome = simulationOutcomeJson
            ).first()

            emit(Result.success(simulationResults to fuckYouMoney))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private suspend fun runSimulation(config: SimulationConfig): SimulationResult = coroutineScope {
        val settings = config.settings
        val dataset = config.dataset
        val inflationData = config.historicalInflation.values.toList()
        val params = config.simulationParams

        val numSimulations = settings.numberOfSimulations.toInt()
        val simulationLength = 100

        val returnsMatrixDeferred = async {
            AnnualReturnsMatrixGenerator.generateMatrices(
                dataset = dataset,
                numSimulations = numSimulations,
                simulationLength = simulationLength,
                daysPerYear = params.daysPerYear
            )
        }

        val inflationMatrixDeferred = async {
            InflationModel.generateInflationMatrix(
                scenarioInflation = settings.inflationModel.lowercase(),
                inflationMean = params.averageInflation,
                historicalInflation = inflationData,
                numSimulations = numSimulations,
                simulationLength = simulationLength
            )
        }

        val (_, annualReturnMatrix) = returnsMatrixDeferred.await()
        val inflationMatrix = inflationMatrixDeferred.await()

        val withdrawalMatrix = WithdrawalCalculator.calculateWithdrawals(
            initialWithdrawal = settings.withdrawals.withoutPension.toDouble(),
            yearsWithoutPension = settings.intervals.yearsInFIRE.toInt(),
            pensionWithdrawal = settings.withdrawals.withPension.toDouble(),
            inflationMatrix = inflationMatrix
        )

        FireSimulator.simulatePortfolio(
            config = config,
            marketReturnsMatrix = annualReturnMatrix,
            withdrawalMatrix = withdrawalMatrix
        )
    }

    private suspend fun calculateOptimizedFuckYouMoneyWithBracket(
        baseConfig: SimulationConfig,
        aInit: Double,
        bInit: Double
    ): Double {
        val successRateCache = mutableMapOf<Double, Double>()
        var a = aInit
        var b = bInit
        var points = generatePoints(a, b)

        suspend fun evaluatePoints(capitals: List<Double>): List<Pair<Double, Double>> = coroutineScope {
            capitals.map { capital ->
                async(Dispatchers.Default) {
                    val rate = successRateCache.getOrPut(capital) {
                        val config = createConfigWithCapital(baseConfig, capital)
                        runSimulation(config).successRate
                    }
                    capital to rate
                }
            }.awaitAll()
        }

        var iteration = 0
        while ((b - a) > CAPITAL_TOLERANCE && iteration < MAX_ITERATIONS) {
            val evaluatedPoints = evaluatePoints(points)
            val sortedPoints = evaluatedPoints.sortedBy { it.first }
            val transitionPoint = sortedPoints.zipWithNext().firstOrNull { (p1, p2) ->
                p1.second < SUCCESS_RATE_THRESHOLD && p2.second >= SUCCESS_RATE_THRESHOLD
            }

            if (transitionPoint != null) {
                val (p1, p2) = transitionPoint
                val estimatedCapital = linearInterpolate(p1.first, p1.second, p2.first, p2.second)
                if (estimatedCapital in (a + CAPITAL_TOLERANCE)..(b - CAPITAL_TOLERANCE)) {
                    val estimatedRate = evaluatePoint(baseConfig, estimatedCapital, successRateCache)
                    if (estimatedRate >= SUCCESS_RATE_THRESHOLD) {
                        b = estimatedCapital
                    } else {
                        a = estimatedCapital
                    }
                }
            } else {
                val successful = evaluatedPoints.filter { it.second >= SUCCESS_RATE_THRESHOLD }
                if (successful.isEmpty()) {
                    a = points.last()
                    b = a * GOLDEN_RATIO
                } else {
                    b = successful.minOf { it.first }
                    val idx = points.indexOfFirst { it >= b }
                    a = if (idx > 0) points[idx - 1] else a
                }
            }
            points = generatePoints(a, b)
            iteration++
        }

        val finalResult = successRateCache.entries
            .filter { it.value >= SUCCESS_RATE_THRESHOLD }
            .minByOrNull { it.key }?.key ?: b

        return finalResult
    }

    private suspend fun evaluatePoint(
        baseConfig: SimulationConfig,
        capital: Double,
        cache: MutableMap<Double, Double>
    ): Double {
        return cache.getOrPut(capital) {
            val config = createConfigWithCapital(baseConfig, capital)
            runSimulation(config).successRate
        }
    }

    private fun generatePoints(a: Double, b: Double): List<Double> {
        return List(PARALLEL_POINTS) { idx ->
            a + (b - a) * idx / (PARALLEL_POINTS - 1)
        }
    }

    private fun linearInterpolate(
        x1: Double, y1: Double,
        x2: Double, y2: Double
    ): Double {
        return x1 + (SUCCESS_RATE_THRESHOLD - y1) * (x2 - x1) / (y2 - y1)
    }

    private fun createConfigWithCapital(baseConfig: SimulationConfig, totalCapital: Double): SimulationConfig {
        val baseTotal = baseConfig.capital.total
        val investedRatio = if (baseTotal > 0) baseConfig.capital.invested / baseTotal else 0.0
        val cashRatio = if (baseTotal > 0) baseConfig.capital.cash / baseTotal else 0.0

        return baseConfig.copy(
            capital = baseConfig.capital.copy(
                invested = totalCapital * investedRatio,
                cash = totalCapital * cashRatio
            )
        )
    }
}