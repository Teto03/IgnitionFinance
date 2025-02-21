package com.unimib.ignitionfinance.domain.usecase.fetch

import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchInflationUseCase @Inject constructor(
    private val inflationRepository: InflationRepository
) {
    private val historicalData = mapOf(
        1955 to 0.023, 1956 to 0.034, 1957 to 0.013, 1958 to 0.028, 1959 to -0.004,
        1960 to 0.023, 1961 to 0.021, 1962 to 0.047, 1963 to 0.075, 1964 to 0.059,
        1965 to 0.046, 1966 to 0.023, 1967 to 0.037, 1968 to 0.014, 1969 to 0.026,
        1970 to 0.050, 1971 to 0.048, 1972 to 0.057, 1973 to 0.108, 1974 to 0.191,
        1975 to 0.170, 1976 to 0.168, 1977 to 0.170, 1978 to 0.121, 1979 to 0.148,
        1980 to 0.212, 1981 to 0.178, 1982 to 0.165, 1983 to 0.147, 1984 to 0.108,
        1985 to 0.092, 1986 to 0.058, 1987 to 0.048
    )

    fun execute(): Flow<Result<Map<Int, Double>>> = flow {
        try {
            inflationRepository.fetchInflationData()
                .collect { result ->
                    result.fold(
                        onSuccess = { inflationList ->
                            val apiData: Map<Int, Double> =
                                inflationList.associate { inflationData ->
                                    inflationData.year.toInt() to (inflationData.inflationRate / 100.0)
                                }

                            val combinedData: Map<Int, Double> = historicalData + apiData
                            emit(Result.success(combinedData))
                        },
                        onFailure = { exception ->
                            emit(Result.failure(exception))
                        }
                    )
                }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}
