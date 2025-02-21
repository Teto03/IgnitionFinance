package com.unimib.ignitionfinance.di

import android.content.Context
import com.unimib.ignitionfinance.data.repository.implementation.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.ExchangeRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.FirestoreRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.InflationRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.LocalDatabaseRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SearchStockRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.StockRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SyncQueueItemRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.UserPreferencesRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.ExchangeRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.repository.interfaces.UserPreferencesRepository
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.remote.mapper.InflationMapper
import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.service.ExchangeService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.remote.service.InflationService
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.local.database.UserDao
import com.unimib.ignitionfinance.data.local.database.SyncQueueItemDao
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.remote.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        authMapper: AuthMapper
    ): AuthRepository = AuthRepositoryImpl(authService, authMapper)

    @Provides
    @Singleton
    fun provideExchangeRepository(
        exchangeService: ExchangeService,
        exchangeMapper: ExchangeMapper
    ): ExchangeRepository = ExchangeRepositoryImpl(exchangeService, exchangeMapper)

    @Provides
    @Singleton
    fun provideInflationRepository(
        inflationService: InflationService,
        inflationMapper: InflationMapper
    ): InflationRepository = InflationRepositoryImpl(inflationService, inflationMapper)

    @Provides
    @Singleton
    fun provideSearchStockRepository(
        searchStockService: SearchStockService,
        searchStockMapper: SearchStockMapper
    ): SearchStockRepository = SearchStockRepositoryImpl(searchStockService, searchStockMapper)

    @Provides
    @Singleton
    fun provideStockRepository(
        stockService: StockService,
        stockMapper: StockMapper,
        @ApplicationContext context: Context
    ): StockRepository = StockRepositoryImpl(stockService, stockMapper, context)

    @Provides
    @Singleton
    fun provideFirestoreRepository(
        firestoreService: FirestoreService
    ): FirestoreRepository = FirestoreRepositoryImpl(firestoreService)

    @Provides
    @Singleton
    fun provideSyncQueueItemRepository(
        syncQueueItemDao: SyncQueueItemDao
    ): SyncQueueItemRepository = SyncQueueItemRepositoryImpl(syncQueueItemDao)

    @Provides
    @Singleton
    fun provideLocalDatabaseRepository(
        userDao: UserDao
    ): LocalDatabaseRepository<User> = LocalDatabaseRepositoryImpl(
        dao = userDao,
        addFn = UserDao::add,
        updateFn = UserDao::update,
        deleteFn = UserDao::delete,
        getByIdFn = UserDao::getUserById,
        getAllFn = UserDao::getAllUsers,
        deleteAllFn = UserDao::deleteAllUsers,
        getUnsyncedFn = UserDao::getUnsyncedUsers,
        getUpdatedAfterFn = UserDao::getUsersUpdatedAfter,
        updateLastSyncFn = UserDao::updateLastSyncTimestamp,
        updateDatasetFn = { id, dataset, timestamp -> userDao.updateDataset(id, dataset, timestamp) },
        updateSimulationOutcomeFn = { id, outcome, timestamp -> userDao.updateSimulationOutcome(id, outcome, timestamp) }
    )

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository = UserPreferencesRepositoryImpl(context)
}
