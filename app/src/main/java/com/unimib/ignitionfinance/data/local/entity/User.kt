package com.unimib.ignitionfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "authData", typeAffinity = ColumnInfo.TEXT) val authData: AuthData,
    @ColumnInfo(name = "settings", typeAffinity = ColumnInfo.TEXT) val settings: Settings,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_sync_timestamp") val lastSyncTimestamp: Long? = null,
    @ColumnInfo(name = "cash") val cash: String = "0",
    @ColumnInfo(name = "product_list") val productList: List<Product> = emptyList(),
    @ColumnInfo(name = "first_added") val firstAdded: Boolean = false,
    @ColumnInfo(name = "dataset") val dataset: List<DailyReturn> = emptyList(),
    @ColumnInfo(name = "simulation_outcome") val simulationOutcome: Pair<List<SimulationResult>, Double?>? = null
)