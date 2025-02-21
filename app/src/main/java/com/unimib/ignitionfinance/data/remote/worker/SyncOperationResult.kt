package com.unimib.ignitionfinance.data.remote.worker

sealed class SyncOperationResult {
    data class Success(val itemId: String) : SyncOperationResult()
    data class Retry(val itemId: String, val attempts: Int) : SyncOperationResult()
    data class Error(val itemId: String, val error: Throwable) : SyncOperationResult()
    data class StaleData(val itemId: String) : SyncOperationResult()
}