package com.unimib.ignitionfinance.data.remote.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncOperationScheduler {
    const val MAX_RETRIES = 3
    const val SYNC_TIMEOUT_MS = 30000L
    const val BATCH_SIZE = 10
    const val BATCH_DELAY_MS = 1000L
    const val INITIAL_BACKOFF_DELAY_MS = 10000L
    const val MIN_BACKOFF_DELAY_MS = 5000L
    const val MAX_BACKOFF_DELAY_MS = 300000L

    inline fun <reified T> scheduleOneTime(
        context: Context,
        constraints: Constraints = getDefaultConstraints(),
        initialDelay: Long = 0L
    ) {
        try {
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker<T>>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    INITIAL_BACKOFF_DELAY_MS.coerceIn(MIN_BACKOFF_DELAY_MS, MAX_BACKOFF_DELAY_MS),
                    TimeUnit.MILLISECONDS
                )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()


            WorkManager.getInstance(context).enqueue(syncWorkRequest)
        } catch (_: Exception) {
        }
    }

    fun getDefaultConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
}