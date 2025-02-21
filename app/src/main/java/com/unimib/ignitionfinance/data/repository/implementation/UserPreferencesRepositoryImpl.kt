package com.unimib.ignitionfinance.data.repository.implementation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.unimib.ignitionfinance.data.repository.interfaces.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private object PreferencesKeys {
    val NET_WORTH_HIDDEN = booleanPreferencesKey("net_worth_hidden")
}

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    override val isNetWorthHidden: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NET_WORTH_HIDDEN] == true
        }

    override suspend fun setNetWorthHidden(hidden: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NET_WORTH_HIDDEN] = hidden
        }
    }
}
