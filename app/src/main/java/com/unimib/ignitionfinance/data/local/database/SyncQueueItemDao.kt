package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus

@Dao
interface SyncQueueItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueItem)

    @Update
    suspend fun update(item: SyncQueueItem)

    @Delete
    suspend fun delete(item: SyncQueueItem)

    @Query("SELECT * FROM sync_queue_items WHERE id = :id")
    suspend fun getById(id: String): SyncQueueItem?

    @Query("SELECT * FROM sync_queue_items WHERE status = :status")
    suspend fun getByStatus(status: SyncStatus): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue_items WHERE status = :status AND collection = :collection")
    suspend fun getByStatusAndCollection(status: SyncStatus, collection: String): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue_items WHERE scheduled_for <= :timestamp AND status = :status")
    suspend fun getPendingItems(timestamp: Long = System.currentTimeMillis(), status: SyncStatus = SyncStatus.PENDING): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue_items ORDER BY created_at ASC")
    suspend fun getAllOrderedByCreatedAt(): List<SyncQueueItem>

    @Query("SELECT COUNT(*) FROM sync_queue_items WHERE status = :status")
    suspend fun countByStatus(status: SyncStatus): Int

    @Query("""
        UPDATE sync_queue_items 
        SET status = :newStatus, 
            attempts = attempts + 1,
            scheduled_for = :nextAttemptTime 
        WHERE id = :id
    """)
    suspend fun updateStatusAndIncrementAttempts(
        id: String,
        newStatus: SyncStatus,
        nextAttemptTime: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM sync_queue_items WHERE status = :status")
    suspend fun deleteByStatus(status: SyncStatus)

    @Query("DELETE FROM sync_queue_items WHERE created_at < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT * FROM sync_queue_items WHERE attempts >= :maxAttempts AND status = :status")
    suspend fun getFailedItems(maxAttempts: Int, status: SyncStatus = SyncStatus.FAILED): List<SyncQueueItem>
}