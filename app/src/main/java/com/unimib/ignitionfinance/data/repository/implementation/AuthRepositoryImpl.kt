package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.utils.AuthServiceException
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authMapper: AuthMapper
) : AuthRepository {

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.signInWithEmailAndPassword(email, password)
            val authData = firebaseUser?.let { authMapper.mapToUserData(it) }
            if (authData != null) {
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: Failed to sign in user")))
            }
        } catch (e: AuthServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.createUserWithEmailAndPassword(email, password)
            val authData = firebaseUser?.let { authMapper.mapToUserData(it) }
            if (authData != null) {
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: Failed to create user")))
            }
        } catch (e: AuthServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun resetPassword(email: String): Flow<Result<Unit>> = flow {
        try {
            authService.resetPassword(email)
            emit(Result.success(Unit))
        } catch (e: AuthServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun signOut(): Flow<Result<Unit>> = flow {
        try {
            authService.signOut()
            emit(Result.success(Unit))
        } catch (e: AuthServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCurrentUser(): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.getCurrentUser()
            if (firebaseUser != null) {
                val authData = authMapper.mapToUserData(firebaseUser)
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: User not logged in")))
            }
        } catch (e: AuthServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}