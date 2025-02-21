package com.unimib.ignitionfinance.data.repository.interfaces

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isNetWorthHidden: Flow<Boolean>
    suspend fun setNetWorthHidden(hidden: Boolean)
}