package com.unimib.ignitionfinance.domain.simulation

import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.random.Random

object AnnualReturnsMatrixGenerator {

    suspend fun generateMatrices(
        dataset: List<DailyReturn>,
        numSimulations: Int,
        simulationLength: Int = 100,
        blockYears: Int = 3,
        daysPerYear: Int = 253
    ): Pair<Array<DoubleArray>, Array<DoubleArray>> {
        if (dataset.size < blockYears * daysPerYear + 1) {
            throw IllegalArgumentException("The dataset is too small for blockYears=$blockYears and daysPerYear=$daysPerYear")
        }

        val maxStartIndex = dataset.size - blockYears * daysPerYear - 1
        val annualReturnsMatrix = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }
        val cumulativeReturnsMatrix = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }

        for (c in 0 until numSimulations) {
            annualReturnsMatrix[0][c] = 1.0
            cumulativeReturnsMatrix[0][c] = 1.0
        }

        withContext(Dispatchers.Default) {
            val jobs = List(numSimulations) { c ->
                async {
                    var currentIndex = 0
                    for (t in 1 until simulationLength) {
                        if ((t - 1) % blockYears == 0) {
                            currentIndex = Random.nextInt(0, maxStartIndex + 1)
                        }
                        annualReturnsMatrix[t][c] = 1.0 + dataset[currentIndex].weightedReturn.toDouble()
                        cumulativeReturnsMatrix[t][c] = cumulativeReturnsMatrix[t - 1][c] * (1.0 + dataset[currentIndex].weightedReturn.toDouble())
                        currentIndex += daysPerYear
                    }
                }
            }
            jobs.awaitAll()
        }

        return Pair(cumulativeReturnsMatrix, annualReturnsMatrix)
    }
}