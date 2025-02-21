package com.unimib.ignitionfinance.di

import com.unimib.ignitionfinance.data.local.utils.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.remote.mapper.InflationMapper
import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideAuthMapper(): AuthMapper = AuthMapper

    @Provides
    @Singleton
    fun provideUserMapper(): UserMapper = UserMapper

    @Provides
    @Singleton
    fun provideUserDataMapper(): UserDataMapper = UserDataMapper

    @Provides
    @Singleton
    fun provideExchangeMapper(): ExchangeMapper = ExchangeMapper

    @Provides
    @Singleton
    fun provideInflationMapper(): InflationMapper = InflationMapper

    @Provides
    @Singleton
    fun provideStockMapper(): StockMapper = StockMapper

    @Provides
    @Singleton
    fun provideSearchStockMapper(): SearchStockMapper = SearchStockMapper
}