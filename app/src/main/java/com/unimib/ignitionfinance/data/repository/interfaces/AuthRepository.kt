package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>>
    suspend fun resetPassword(email: String): Flow<Result<Unit>>
    suspend fun signOut(): Flow<Result<Unit>>
    suspend fun getCurrentUser(): Flow<Result<AuthData>>
}