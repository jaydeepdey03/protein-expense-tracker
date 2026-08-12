package com.jaydeep.trackingapp.core.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jaydeep.trackingapp.core.data.local.dao.ExpenseDao
import com.jaydeep.trackingapp.core.data.local.dao.ProteinDao
import com.jaydeep.trackingapp.core.data.local.entities.ExpenseEntity
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import com.jaydeep.trackingapp.core.data.remote.api.ExpenseApi
import com.jaydeep.trackingapp.core.data.remote.api.ProteinApi
import com.jaydeep.trackingapp.core.data.remote.dto.CreateExpenseEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.CreateProteinEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateProteinEntryRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val expenseDao: ExpenseDao,
    private val proteinDao: ProteinDao,
    private val expenseApi: ExpenseApi,
    private val proteinApi: ProteinApi
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

    /**
     * Sync all locally-created/updated expenses.
     */
    private suspend fun syncExpenses() {
        val unsyncedExpenses = expenseDao.getUnsynced()

        if (unsyncedExpenses.isEmpty()) {
            Log.d(TAG, "No expenses to sync")
            return
        }

        Log.d(TAG, "Syncing ${unsyncedExpenses.size} expenses")

        unsyncedExpenses.forEach { entity ->
            syncExpense(entity)
        }
    }

    private suspend fun syncExpense(entity: ExpenseEntity) {
        Log.d(TAG, "Syncing expense: ${entity.id}")

        val response = expenseApi.createEntry(
            CreateExpenseEntryRequest(
                description = entity.title,
                amount = entity.amount,
                category = entity.category,
                currency = entity.currency,
                entryDate = entity.date
            )
        )

        if (!response.isSuccessful) {
            throw IOException(
                "Expense sync failed. " +
                        "id=${entity.id}, " +
                        "code=${response.code()}, " +
                        "message=${response.message()}"
            )
        }

        val serverDto = response.body()
            ?: throw IOException(
                "Expense sync returned empty response. id=${entity.id}"
            )

        /*
         * Ideally, update the existing local entity instead of
         * deleting and inserting a new one.
         *
         * This requires something like:
         *
         * expenseDao.markSynced(
         *     localId = entity.id,
         *     remoteId = serverDto.id
         * )
         */

        expenseDao.deleteById(entity.id)

        expenseDao.insert(
            ExpenseEntity(
                id = serverDto.id,
                userId = entity.userId,
                title = serverDto.description,
                amount = serverDto.amount,
                currency = serverDto.currency,
                date = serverDto.entryDate,
                category = serverDto.category,
                notes = entity.notes,
                createdAt = entity.createdAt,
                updatedAt = serverDto.entryDate,
                isSynced = true
            )
        )

        Log.d(TAG, "Expense synced successfully: ${entity.id}")
    }

    /**
     * Sync all locally-created/updated protein entries.
     */
    private suspend fun syncProteins() {
        val unsyncedProteins = proteinDao.getUnsynced()

        if (unsyncedProteins.isEmpty()) {
            Log.d(TAG, "No proteins to sync")
            return
        }

        Log.d(TAG, "Syncing ${unsyncedProteins.size} protein entries")

        unsyncedProteins.forEach { entity ->
            syncProtein(entity)
        }
    }

    private suspend fun syncProtein(entity: ProteinEntity) {
        Log.d(TAG, "Syncing protein: ${entity.id}")

        if (entity.remoteId == null) {
            createProtein(entity)
        } else {
            updateProtein(entity)
        }
    }

    private suspend fun createProtein(entity: ProteinEntity) {
        val response = proteinApi.createEntry(
            CreateProteinEntryRequest(
                foodName = entity.foodName,
                gramsConsumed = entity.gramsConsumed,
                proteinGrams = entity.proteinGrams,
                entryDate = entity.date
            )
        )

        if (!response.isSuccessful) {
            throw IOException(
                "Protein creation failed. " +
                        "id=${entity.id}, " +
                        "code=${response.code()}, " +
                        "message=${response.message()}"
            )
        }

        val serverDto = response.body()
            ?: throw IOException(
                "Protein creation returned empty response. id=${entity.id}"
            )

        /*
         * Ideally update the existing row with the remote ID
         * rather than delete + insert.
         */

        proteinDao.deleteById(entity.id)

        proteinDao.insert(
            ProteinEntity(
                remoteId = serverDto.id,
                foodName = serverDto.foodName,
                gramsConsumed = serverDto.gramsConsumed,
                proteinGrams = serverDto.proteinGrams,
                date = serverDto.entryDate,
                isSynced = true
            )
        )

        Log.d(TAG, "Protein created successfully: ${entity.id}")
    }

    private suspend fun updateProtein(entity: ProteinEntity) {
        val remoteId = entity.remoteId
            ?: return

        val response = proteinApi.updateEntry(
            entryId = remoteId,
            request = UpdateProteinEntryRequest(
                proteinGrams = entity.proteinGrams
            )
        )

        if (!response.isSuccessful) {
            throw IOException(
                "Protein update failed. " +
                        "localId=${entity.id}, " +
                        "remoteId=$remoteId, " +
                        "code=${response.code()}, " +
                        "message=${response.message()}"
            )
        }

        proteinDao.markSynced(entity.id)

        Log.d(TAG, "Protein updated successfully: $remoteId")
    }

    companion object {

        private const val TAG = "SyncWorker"

        const val WORK_NAME = "tracker_sync"
        private const val IMMEDIATE_WORK_NAME = "tracker_sync_now"

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

        /**
         * Trigger synchronization immediately.
         *
         * Unique work prevents multiple sync workers
         * from being queued simultaneously.
         */
        fun enqueueNow(workManager: WorkManager) {
            workManager.enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                oneTimeRequest()
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

        private fun oneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SyncWorker>()
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