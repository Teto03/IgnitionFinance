package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.local.database.SyncQueueItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import javax.inject.Inject

class SyncQueueItemRepositoryImpl @Inject constructor(
    private val syncQueueItemDao: SyncQueueItemDao
) : SyncQueueItemRepository {

    override suspend fun insert(item: SyncQueueItem) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.insert(item)
        }
    }

    override suspend fun update(item: SyncQueueItem) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.update(item)
        }
    }

    override suspend fun delete(item: SyncQueueItem) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.delete(item)
        }
    }

    override suspend fun getById(id: String): SyncQueueItem? {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getById(id)
        }
    }

    override suspend fun getByStatus(status: SyncStatus): List<SyncQueueItem> {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getByStatus(status)
        }
    }

    override suspend fun getByStatusAndCollection(status: SyncStatus, collection: String): List<SyncQueueItem> {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getByStatusAndCollection(status, collection)
        }
    }

    override suspend fun getAllOrderedByCreatedAt(): List<SyncQueueItem> {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getAllOrderedByCreatedAt()
        }
    }

    override suspend fun countByStatus(status: SyncStatus): Int {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.countByStatus(status)
        }
    }

    override suspend fun updateStatusAndIncrementAttempts(
        id: String,
        newStatus: SyncStatus,
        nextAttemptTime: Long
    ) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.updateStatusAndIncrementAttempts(id, newStatus, nextAttemptTime)
        }
    }

    override suspend fun deleteByStatus(status: SyncStatus) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.deleteByStatus(status)
        }
    }

    override suspend fun deleteOlderThan(timestamp: Long) {
        withContext(Dispatchers.IO) {
            syncQueueItemDao.deleteOlderThan(timestamp)
        }
    }

    override suspend fun getPendingItems(timestamp: Long): List<SyncQueueItem> {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getPendingItems(timestamp, SyncStatus.PENDING)
        }
    }

    override suspend fun getFailedItems(maxAttempts: Int): List<SyncQueueItem> {
        return withContext(Dispatchers.IO) {
            syncQueueItemDao.getFailedItems(maxAttempts, SyncStatus.FAILED)
        }
    }
}