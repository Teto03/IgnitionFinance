package com.unimib.ignitionfinance.domain.usecase.auth

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.utils.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.remote.worker.SyncOperationScheduler
import com.unimib.ignitionfinance.domain.usecase.settings.SetDefaultSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    @ApplicationContext private val context: Context
) {
    fun handleUserStorage(
        authData: AuthData,
        name: String,
        surname: String
    ): Flow<Result<Unit?>> = flow {
        try {
            val existingUserResult = firestoreRepository.getDocumentById("users", authData.id).firstOrNull()

            if (existingUserResult?.getOrNull() != null) {
                executeExistingUser(existingUserResult.getOrNull()).collect {
                    emit(it)
                }
            } else {
                val currentTime = System.currentTimeMillis()
                val settings = SetDefaultSettingsUseCase().execute()
                val user = User(
                    authData = authData,
                    id = authData.id,
                    name = name,
                    settings = settings,
                    surname = surname,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                executeNewUser(user).collect {
                    emit(it)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun executeExistingUser(user: Map<String, Any>?): Flow<Result<Unit?>> = flow {
        try {
            val id = (user?.get("authData") as? Map<*, *>)?.get("id") as? String
                ?: throw IllegalArgumentException("User ID is missing or invalid")

            val localUser = localDatabaseRepository.getById(id).first()
            val updatedUserData = UserDataMapper.mapDocumentToUserData(user)
            val updatedUser = UserMapper.mapUserDataToUser(updatedUserData)

            if (localUser.getOrNull() != null) {
                val localTimestamp = localUser.getOrNull()?.updatedAt ?: 0
                val remoteTimestamp = updatedUser.updatedAt

                if (remoteTimestamp > localTimestamp) {
                    val mergedUser = updatedUser.copy(
                        createdAt = localUser.getOrNull()?.createdAt ?: updatedUser.createdAt
                    )
                    localDatabaseRepository.update(mergedUser).first()

                    syncQueueItemRepository.getPendingItems().filter {
                        it.id == id
                    }.forEach { item ->
                        syncQueueItemRepository.delete(item)
                    }
                } else if (remoteTimestamp < localTimestamp) {
                    val syncQueueItem = createSyncQueueItem(localUser.getOrNull()!!)
                    syncQueueItemRepository.insert(syncQueueItem)

                    withContext(Dispatchers.IO) {
                        SyncOperationScheduler.scheduleOneTime<User>(context)
                    }
                }
            } else {
                localDatabaseRepository.add(updatedUser).first()
            }

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun executeNewUser(user: User): Flow<Result<Unit?>> = flow {
        val syncQueueItem = createSyncQueueItem(user)

        try {
            coroutineScope {
                val currentTime = System.currentTimeMillis()
                val userWithTimestamps = user.copy(
                    createdAt = currentTime,
                    updatedAt = currentTime
                )

                val localDbDeferred = async {
                    localDatabaseRepository.add(userWithTimestamps).first()
                }

                val syncQueueDeferred = async {
                    syncQueueItemRepository.insert(syncQueueItem)
                }

                val localResult = localDbDeferred.await()
                syncQueueDeferred.await()

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime<User>(context)
                }

                emit(Result.success(localResult.getOrNull()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)
        val documentId = userData.authData.id

        return SyncQueueItem(
            id = documentId,
            collection = "users",
            payload = document,
            operationType = "ADD",
            status = SyncStatus.PENDING
        )
    }
}