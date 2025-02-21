package com.unimib.ignitionfinance.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unimib.ignitionfinance.data.local.converter.SyncQueueItemConverter
import com.unimib.ignitionfinance.data.local.converter.UserConverter
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User

@Database(
    entities = [
        User::class,
        SyncQueueItem::class,
    ],
    version = 16,
    exportSchema = true
)
@TypeConverters(
    UserConverter::class,
    SyncQueueItemConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun syncQueueItemDao(): SyncQueueItemDao
}