package com.jaydeep.trackingapp.core.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val expenseRepository: ExpenseRepository,
    private val proteinRepository: ProteinRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Sync started. Attempt: $runAttemptCount")

        return try {
            syncExpenses()
            syncProteins()

            Log.d(TAG, "Sync completed successfully")

            Result.success()
        } catch (e: IOException) {
            Log.e(TAG, "Network error during sync", e)

            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sync", e)

            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun syncExpenses() {
        expenseRepository.pushUnsyncedExpenses()
    }

    private suspend fun syncProteins() {
        proteinRepository.pushUnsyncedProteins()
    }

    companion object {

        private const val TAG = "SyncWorker"

        const val WORK_NAME = "tracker_sync"

        private const val MAX_RETRIES = 3

        /**
         * Schedule periodic synchronization.
         *
         * WorkManager decides the exact execution time.
         */
        fun schedule(workManager: WorkManager) {
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest()
            )
        }

        private fun periodicRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SyncWorker>(
                1,
                TimeUnit.HOURS
            )
                .setConstraints(syncConstraints())
                .build()
        }

        private fun syncConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }
    }
}