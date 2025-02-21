package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.model.user.Settings

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun add(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User) {
        updateNoSimulation(
            id = user.id,
            name = user.name,
            surname = user.surname,
            authData = user.authData,
            settings = user.settings,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            lastSyncTimestamp = user.lastSyncTimestamp,
            cash = user.cash,
            productList = user.productList,
            firstAdded = user.firstAdded
        )
    }


    @Query("""
        UPDATE users 
        SET dataset = :dataset,
            updated_at = :timestamp
        WHERE id = :userId
    """)
    suspend fun updateDataset(
        userId: String,
        dataset: String?,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE users 
        SET simulation_outcome = :simulationOutcome,
            updated_at = :timestamp
        WHERE id = :userId
    """)
    suspend fun updateSimulationOutcome(
        userId: String,
        simulationOutcome: String?,
        timestamp: Long = System.currentTimeMillis()
    )


    @Query("UPDATE users SET updated_at = :timestamp WHERE id = :userId")
    suspend fun updateTimestamp(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE updated_at > :timestamp")
    suspend fun getUsersUpdatedAfter(timestamp: Long): List<User>

    @Query("UPDATE users SET last_sync_timestamp = :timestamp WHERE id = :userId")
    suspend fun updateLastSyncTimestamp(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("""
    UPDATE users 
    SET name = :name,
        surname = :surname,
        authData = :authData,
        settings = :settings,
        created_at = :createdAt,
        updated_at = :updatedAt,
        last_sync_timestamp = :lastSyncTimestamp,
        cash = :cash,
        product_list = :productList,
        first_added = :firstAdded
    WHERE id = :id
""")
    suspend fun updateNoSimulation(
        id: String,
        name: String,
        surname: String,
        authData: AuthData,
        settings: Settings,
        createdAt: Long,
        updatedAt: Long,
        lastSyncTimestamp: Long?,
        cash: String,
        productList: List<Product>,
        firstAdded: Boolean
    )

    @Query("SELECT * FROM users WHERE last_sync_timestamp < updated_at OR last_sync_timestamp IS NULL")
    suspend fun getUnsyncedUsers(): List<User>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}