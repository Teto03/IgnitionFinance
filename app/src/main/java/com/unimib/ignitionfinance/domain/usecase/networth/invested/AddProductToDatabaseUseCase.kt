package com.unimib.ignitionfinance.domain.usecase.networth.invested

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.utils.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.remote.worker.SyncOperationScheduler
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchSearchStockDataUseCase
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AddProductToDatabaseUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val fetchSearchStockDataUseCase: FetchSearchStockDataUseCase,
    private val networkUtils: NetworkUtils,
    @ApplicationContext private val context: Context
) {
    fun handleProductStorage(product: Product, apiKey: String): Flow<Result<Unit?>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            val updatedProduct = if (networkUtils.isNetworkAvailable()) {
                val searchStockResult = fetchSearchStockDataUseCase.execute(product.ticker, apiKey).first()
                val searchStock = searchStockResult.getOrNull()
                    ?: throw IllegalStateException("Failed to fetch stock data for ${product.ticker}")

                product.copy(
                    currency = searchStock.currency,
                    symbol = searchStock.symbol
                )
            } else {
                product
            }

            val existingProductIndex = currentUser.productList.indexOfFirst { it.ticker == updatedProduct.ticker }

            if (existingProductIndex != -1) {
                executeExistingProduct(currentUser, updatedProduct, existingProductIndex).collect {
                    emit(it)
                }
            } else {
                executeNewProduct(currentUser, updatedProduct).collect {
                    emit(it)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun executeExistingProduct(
        currentUser: User,
        product: Product,
        index: Int
    ): Flow<Result<Unit?>> = flow {
        try {
            val updatedProductList = currentUser.productList.toMutableList()
            updatedProductList[index] = product

            val updatedUser = currentUser.copy(
                productList = updatedProductList,
                updatedAt = System.currentTimeMillis()
            )

            localDatabaseRepository.update(updatedUser).first()

            val syncQueueItem = createSyncQueueItem(updatedUser)
            syncQueueItemRepository.insert(syncQueueItem)

            withContext(Dispatchers.IO) {
                SyncOperationScheduler.scheduleOneTime<User>(context)
            }

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun executeNewProduct(
        currentUser: User,
        product: Product
    ): Flow<Result<Unit?>> = flow {
        try {
            coroutineScope {
                val updatedProductList = currentUser.productList + product
                val updatedUser = currentUser.copy(
                    productList = updatedProductList,
                    updatedAt = System.currentTimeMillis(),
                )

                val localDbDeferred = async {
                    localDatabaseRepository.update(updatedUser).first()
                }

                val syncQueueItem = createSyncQueueItem(updatedUser)
                val syncQueueDeferred = async {
                    syncQueueItemRepository.insert(syncQueueItem)
                }

                localDbDeferred.await()
                syncQueueDeferred.await()

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime<User>(context)
                }

                delay(500)

                emit(Result.success(Unit))
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

        return SyncQueueItem(
            id = user.id,
            collection = "users",
            payload = document,
            operationType = "UPDATE",
            status = SyncStatus.PENDING
        )
    }
}