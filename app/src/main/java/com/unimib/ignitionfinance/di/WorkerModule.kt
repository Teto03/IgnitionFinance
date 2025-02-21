package com.unimib.ignitionfinance.di

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.remote.worker.SyncWorkerFactory
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideSyncWorkerFactory(
        syncQueueItemRepository: SyncQueueItemRepository,
        firestoreRepository: FirestoreRepository,
        localDatabaseRepository: LocalDatabaseRepository<User>
    ): SyncWorkerFactory = SyncWorkerFactory(
        syncQueueItemRepository,
        firestoreRepository,
        localDatabaseRepository
    )
}