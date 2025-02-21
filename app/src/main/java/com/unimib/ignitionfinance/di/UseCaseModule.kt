package com.unimib.ignitionfinance.di

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.utils.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchSearchStockDataUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.ResetPasswordUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.UpdateUserSettingsUseCase
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase = LoginUserUseCase(authRepository)

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase = ResetPasswordUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase = RegisterNewUserUseCase(authRepository)

    @Provides
    @Singleton
    fun provideAddUserToDatabaseUseCase(
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        localDatabaseRepository: LocalDatabaseRepository<User>,
        firestoreRepository: FirestoreRepository,
        syncQueueItemRepository: SyncQueueItemRepository,
        context: Context
    ): AddUserToDatabaseUseCase = AddUserToDatabaseUseCase(
        userMapper,
        userDataMapper,
        localDatabaseRepository,
        firestoreRepository,
        syncQueueItemRepository,
        context
    )

    @Provides
    @Singleton
    fun provideUpdateUserSettingsUseCase(
        authRepository: AuthRepository,
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        localDatabaseRepository: LocalDatabaseRepository<User>,
        syncQueueItemRepository: SyncQueueItemRepository,
        getUserSettingsUseCase: GetUserSettingsUseCase,
        context: Context
    ): UpdateUserSettingsUseCase = UpdateUserSettingsUseCase(
        authRepository,
        userMapper,
        userDataMapper,
        localDatabaseRepository,
        syncQueueItemRepository,
        getUserSettingsUseCase,
        context
    )

    @Provides
    @Singleton
    fun provideFetchSearchStockDataUseCase(
        searchStockRepository: SearchStockRepository
    ): FetchSearchStockDataUseCase = FetchSearchStockDataUseCase(searchStockRepository)

    @Provides
    @Singleton
    fun provideGetProductListUseCase(
        networkUtils: NetworkUtils,
        authRepository: AuthRepository,
        localDatabaseRepository: LocalDatabaseRepository<User>,
        firestoreRepository: FirestoreRepository,
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        fetchSearchStockDataUseCase: FetchSearchStockDataUseCase,
        syncQueueItemRepository: SyncQueueItemRepository,
        context: Context
    ): GetProductListUseCase = GetProductListUseCase(
        networkUtils,
        authRepository,
        localDatabaseRepository,
        firestoreRepository,
        userMapper,
        userDataMapper,
        fetchSearchStockDataUseCase,
        syncQueueItemRepository,
        context
    )
}