package com.unimib.ignitionfinance.data.remote.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.*

@HiltWorker
class SyncWorker<T> @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val firestoreRepository: FirestoreRepository,
    private val localRepository: LocalDatabaseRepository<T>
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            cleanupStuckSyncingItems()

            val currentTime = System.currentTimeMillis()
            val pendingItems = syncQueueItemRepository.getPendingItems(currentTime)

            if (pendingItems.isEmpty()) {
                return@coroutineScope Result.success()
            }

            val results = processBatches(pendingItems)

            val errorCount = results.count { it is SyncOperationResult.Error }
            results.count { it is SyncOperationResult.Success }
            results.count { it is SyncOperationResult.Retry }
            val staleCount = results.count { it is SyncOperationResult.StaleData }


            handleFailedItems()

            return@coroutineScope when {
                errorCount > 0 -> {
                    Result.retry()
                }
                staleCount > 0 -> {
                    Result.success()
                }
                else -> {
                    Result.success()
                }
            }
        } catch (e: Exception) {
            handleWorkerError()
        }
    }

    private suspend fun cleanupStuckSyncingItems() {
        val stuckSyncingItems = syncQueueItemRepository.getByStatus(SyncStatus.SYNCING)

        val currentTime = System.currentTimeMillis()
        stuckSyncingItems.forEach { item ->
            if (currentTime - item.createdAt > SyncOperationScheduler.SYNC_TIMEOUT_MS) {
                val nextAttemptTime = calculateNextAttemptTime(item.attempts + 1)
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.ABANDONED,
                    nextAttemptTime
                )
            }
        }
    }

    private suspend fun handleFailedItems() {
        val failedItems = syncQueueItemRepository.getFailedItems(SyncOperationScheduler.MAX_RETRIES)
        failedItems.forEach { item ->
            syncQueueItemRepository.delete(item)
        }
    }

    private suspend fun processBatches(items: List<SyncQueueItem>): List<SyncOperationResult> {
        return items.chunked(SyncOperationScheduler.BATCH_SIZE).flatMap { batch ->
            batch.map { item ->
                processItem(item)
            }.also {
                delay(SyncOperationScheduler.BATCH_DELAY_MS)
            }
        }
    }

    private suspend fun processItem(item: SyncQueueItem): SyncOperationResult {

        syncQueueItemRepository.updateStatusAndIncrementAttempts(
            item.id,
            SyncStatus.SYNCING,
            System.currentTimeMillis()
        )

        return try {
            val result = when (item.operationType) {
                "ADD" -> performAddOperation(item)
                "UPDATE" -> performUpdateOperation(item)
                "DELETE" -> performDeleteOperation(item)
                else -> throw IllegalArgumentException("Unknown operation type: ${item.operationType}")
            }

            handleOperationResult(item, result)
            result
        } catch (e: Exception) {
            handleItemError(item, e)
        }
    }

    private suspend fun handleOperationResult(item: SyncQueueItem, result: SyncOperationResult) {
        when (result) {
            is SyncOperationResult.Success -> {
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.SUCCEEDED,
                    System.currentTimeMillis()
                )

                localRepository.updateLastSyncTimestamp(item.id).first().fold(
                    onSuccess = {
                    },
                    onFailure = {
                    }
                )
                syncQueueItemRepository.delete(item)
            }
            is SyncOperationResult.StaleData -> {
                syncQueueItemRepository.delete(item)
            }
            else -> {
            }
        }
    }

    private suspend fun performAddOperation(item: SyncQueueItem): SyncOperationResult {

        return firestoreRepository.addDocument(
            collectionPath = item.collection,
            data = item.payload,
            documentId = item.id
        ).first().fold(
            onSuccess = {
                SyncOperationResult.Success(item.id)
            },
            onFailure = { error ->
                throw error
            }
        )
    }

    private suspend fun performUpdateOperation(item: SyncQueueItem): SyncOperationResult {

        try {
            val currentDocResult = firestoreRepository.getDocumentById(
                collectionPath = item.collection,
                documentId = item.id
            ).first()

            val currentDoc = currentDocResult.getOrNull()
                ?: throw IllegalStateException("Remote document not found or null")


            val remoteTimestamp = (currentDoc["updatedAt"] as? Double)?.toLong()
                ?: throw IllegalStateException("Remote document missing updatedAt timestamp")
            val localTimestamp = item.createdAt

            if (remoteTimestamp > localTimestamp) {
                return SyncOperationResult.StaleData(item.id)
            }

            return firestoreRepository.updateDocument(
                collectionPath = item.collection,
                data = item.payload,
                documentId = item.id
            ).first().fold(
                onSuccess = {
                    SyncOperationResult.Success(item.id)
                },
                onFailure = { error ->
                    throw error
                }
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun performDeleteOperation(item: SyncQueueItem): SyncOperationResult {

        return firestoreRepository.deleteDocument(
            collectionPath = item.collection,
            documentId = item.id
        ).first().fold(
            onSuccess = {
                SyncOperationResult.Success(item.id)
            },
            onFailure = { error ->
                throw error
            }
        )
    }

    private suspend fun handleItemError(item: SyncQueueItem, error: Throwable): SyncOperationResult {

        return when {
            item.attempts < SyncOperationScheduler.MAX_RETRIES -> {
                val nextAttemptTime = calculateNextAttemptTime(item.attempts + 1)
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.PENDING,
                    nextAttemptTime
                )
                SyncOperationResult.Retry(item.id, item.attempts + 1)
            }
            else -> {
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.FAILED,
                    System.currentTimeMillis()
                )
                SyncOperationResult.Error(item.id, error)
            }
        }
    }

    private fun calculateNextAttemptTime(attempts: Int): Long {
        val backoffMs = SyncOperationScheduler.INITIAL_BACKOFF_DELAY_MS * (1 shl (attempts - 1))
        return System.currentTimeMillis() + backoffMs
    }

    private fun handleWorkerError(): Result {
        return Result.retry()
    }
}