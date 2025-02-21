package com.unimib.ignitionfinance.di

import android.content.Context
import androidx.room.Room
import com.unimib.ignitionfinance.data.local.database.AppDatabase
import com.unimib.ignitionfinance.data.local.database.SyncQueueItemDao
import com.unimib.ignitionfinance.data.local.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "ignition_finance_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideSyncQueueItemDao(appDatabase: AppDatabase): SyncQueueItemDao = appDatabase.syncQueueItemDao()
}