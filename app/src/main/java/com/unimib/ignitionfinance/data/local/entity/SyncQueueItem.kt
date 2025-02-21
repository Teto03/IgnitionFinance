package com.unimib.ignitionfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unimib.ignitionfinance.data.local.utils.SyncStatus

@Entity(tableName = "sync_queue_items")
data class SyncQueueItem (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "collection") val collection: String,
    @ColumnInfo(name = "payload") val payload: Map<String, Any>,
    @ColumnInfo(name = "operation") val operationType: String,
    @ColumnInfo(name = "status") val status: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "attempts") val attempts: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "scheduled_for") val scheduledFor: Long = System.currentTimeMillis()
)