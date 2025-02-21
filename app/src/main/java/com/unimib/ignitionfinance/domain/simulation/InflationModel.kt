package com.unimib.ignitionfinance.domain.simulation

import kotlinx.coroutines.*
import kotlin.math.*

object InflationModel {
    private fun List<Double>.variance(): Double {
        val mean = this.average()
        return this.map { (it - mean).pow(2) }.average()
    }

    suspend fun generateInflationMatrix(
        scenarioInflation: String,
        inflationMean: Double,
        historicalInflation: List<Double>,
        numSimulations: Int,
        simulationLength: Int = 100
    ): Array<DoubleArray> = withContext(Dispatchers.Default) {

        val inflationMatrix = Array(simulationLength) { DoubleArray(numSimulations) }

        when (scenarioInflation.lowercase()) {
            "normal" -> {
                val jobs = List(simulationLength) { t ->
                    async {
                        for (s in 0 until numSimulations) {
                            inflationMatrix[t][s] = historicalInflation[kotlin.random.Random.nextInt(historicalInflation.size)]
                        }
                    }
                }
                jobs.awaitAll()
            }
            "scale" -> {
                val scaleFactor = inflationMean / historicalInflation.average()
                val jobs = List(simulationLength) { t ->
                    async {
                        for (s in 0 until numSimulations) {
                            inflationMatrix[t][s] = historicalInflation[kotlin.random.Random.nextInt(historicalInflation.size)] * scaleFactor
                        }
                    }
                }
                jobs.awaitAll()
            }
            "lognormal" -> {
                val variance = historicalInflation.variance()
                var muCalc = ln(inflationMean)
                var sigmaCalc = ln((1 + sqrt(1 + 4 * variance / exp(2 * muCalc))) / 2)
                muCalc = ln(inflationMean) - (sigmaCalc * sigmaCalc) / 2
                sigmaCalc = ln((1 + sqrt(1 + 4 * variance / exp(2 * muCalc))) / 2)
                muCalc = ln(inflationMean) - (sigmaCalc * sigmaCalc) / 2

                val rand = java.util.Random()
                val jobs = List(simulationLength) { t ->
                    async {
                        for (s in 0 until numSimulations) {
                            inflationMatrix[t][s] = exp(muCalc + sigmaCalc * rand.nextGaussian())
                        }
                    }
                }
                jobs.awaitAll()
            }
            else -> {
                val jobs = List(simulationLength) { t ->
                    async {
                        for (s in 0 until numSimulations) {
                            inflationMatrix[t][s] = inflationMean
                        }
                    }
                }
                jobs.awaitAll()
            }
        }
        inflationMatrix
    }
}