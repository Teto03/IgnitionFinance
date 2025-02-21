package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.service.InflationService
import com.unimib.ignitionfinance.data.remote.mapper.InflationMapper
import com.unimib.ignitionfinance.data.remote.model.api.InflationData
import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InflationRepositoryImpl @Inject constructor(
    private val inflationService: InflationService,
    private val inflationApiMapper: InflationMapper
) : InflationRepository {
    override suspend fun fetchInflationData(): Flow<Result<List<InflationData>>> = flow {
        try {
            val response = inflationService.getInflationData()
            if (response.isSuccessful) {
                val inflationData = response.body()
                if (inflationData != null) {
                    emit(Result.success(inflationApiMapper.mapToDomain(inflationData)))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Error: Failed to fetch inflation data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}