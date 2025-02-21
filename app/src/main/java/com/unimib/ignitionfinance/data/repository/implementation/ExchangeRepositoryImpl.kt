package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.service.ExchangeService
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.remote.model.api.ExchangeData
import com.unimib.ignitionfinance.data.repository.interfaces.ExchangeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeService: ExchangeService,
    private val exchangeMapper: ExchangeMapper
) : ExchangeRepository {

    override suspend fun fetchExchangeData(seriesKey: String): Flow<Result<List<ExchangeData>>> = flow {
        try {
            val response = exchangeService.getExchangeRate(seriesKey = seriesKey)

            if (response.isSuccessful) {
                val exchangeData = response.body()
                if (exchangeData != null) {
                    val mappedData = exchangeMapper.mapToDomain(exchangeData)
                    emit(Result.success(mappedData))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Error: Failed to fetch exchange data - ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}