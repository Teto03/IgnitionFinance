package com.unimib.ignitionfinance.domain.usecase.networth.invested

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.utils.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.remote.worker.SyncOperationScheduler
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchSearchStockDataUseCase
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetProductListUseCase @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val fetchSearchStockDataUseCase: FetchSearchStockDataUseCase,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    @ApplicationContext private val context: Context
) {
    fun execute(apiKey: String): Flow<Result<List<Product>>> = flow {
        val currentUserResult = authRepository.getCurrentUser().first()
        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Failed to get current user")

        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing")

        val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
            ?: throw IllegalStateException("User not found in local database")

        val isOnline = networkUtils.isNetworkAvailable()

        val remoteUser = if (isOnline) {
            try {
                firestoreRepository.getDocumentById("users", userId)
                    .firstOrNull()
                    ?.getOrNull()
                    ?.let { userDataMapper.mapDocumentToUserData(it) }
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }

        val productList = when {
            remoteUser != null &&
                    (remoteUser.updatedAt >= (localUser.lastSyncTimestamp ?: 0)) &&
                    (remoteUser.updatedAt >= localUser.updatedAt) -> {
                val updatedLocalUser = localUser.copy(
                    productList = remoteUser.productList,
                    updatedAt = remoteUser.updatedAt,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                remoteUser.productList
            }
            else -> localUser.productList
        }

        if (isOnline) {
            var needsUpdate = false
            val updatedProducts = productList.map { product ->
                if (product.currency.isEmpty() || product.symbol.isEmpty()) {
                    needsUpdate = true
                    try {
                        val searchResult = fetchSearchStockDataUseCase.execute(product.ticker, apiKey).first()
                        val searchStock = searchResult.getOrNull()
                            ?: throw IllegalStateException("Failed to fetch stock data for ${product.ticker}")

                        product.copy(
                            currency = searchStock.currency,
                            symbol = searchStock.symbol
                        )
                    } catch (_: Exception) {
                        product
                    }
                } else {
                    product
                }
            }

            if (needsUpdate) {
                val updatedUser = localUser.copy(
                    productList = updatedProducts,
                    updatedAt = System.currentTimeMillis()
                )
                localDatabaseRepository.update(updatedUser).first()

                val syncQueueItem = createSyncQueueItem(updatedUser)
                syncQueueItemRepository.insert(syncQueueItem)

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime<User>(context)
                }

                emit(Result.success(updatedProducts))
                return@flow
            }
        }

        emit(Result.success(productList))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)

        return SyncQueueItem(
            id = user.id,
            collection = "users",
            payload = document,
            operationType = "UPDATE",
            status = SyncStatus.PENDING
        )
    }
}