package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus

interface SyncQueueItemRepository {
    suspend fun insert(item: SyncQueueItem)
    suspend fun update(item: SyncQueueItem)
    suspend fun delete(item: SyncQueueItem)
    suspend fun getById(id: String): SyncQueueItem?
    suspend fun getByStatus(status: SyncStatus): List<SyncQueueItem>
    suspend fun getByStatusAndCollection(status: SyncStatus, collection: String): List<SyncQueueItem>
    suspend fun getAllOrderedByCreatedAt(): List<SyncQueueItem>
    suspend fun countByStatus(status: SyncStatus): Int
    suspend fun updateStatusAndIncrementAttempts(
        id: String,
        newStatus: SyncStatus,
        nextAttemptTime: Long = System.currentTimeMillis()
    )
    suspend fun deleteByStatus(status: SyncStatus)
    suspend fun deleteOlderThan(timestamp: Long)
    suspend fun getPendingItems(timestamp: Long = System.currentTimeMillis()): List<SyncQueueItem>
    suspend fun getFailedItems(maxAttempts: Int): List<SyncQueueItem>
}
