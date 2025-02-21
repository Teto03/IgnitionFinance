package com.unimib.ignitionfinance.data.repository.interfaces

import kotlinx.coroutines.flow.Flow

interface LocalDatabaseRepository<T> {
    suspend fun getById(id: String): Flow<Result<T?>>
    suspend fun getAll(): Flow<Result<List<T>>>
    suspend fun add(entity: T): Flow<Result<Unit>>
    suspend fun update(entity: T): Flow<Result<Unit>>
    suspend fun delete(entity: T): Flow<Result<Unit>>
    suspend fun deleteAll(): Flow<Result<Unit>>
    suspend fun getUpdatedAfter(timestamp: Long): Flow<Result<List<T>>>
    suspend fun getUnsyncedEntities(): Flow<Result<List<T>>>
    suspend fun updateLastSyncTimestamp(id: String, timestamp: Long = System.currentTimeMillis()): Flow<Result<Unit>>
    suspend fun exists(id: String): Flow<Result<Boolean>>
    suspend fun updateDataset(id: String, dataset: String?): Flow<Result<Unit>>
    suspend fun updateSimulationOutcome(id: String, outcome: String?): Flow<Result<Unit>>
}