package com.jaydeep.trackingapp.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
        return try {
            syncExpenses()
            syncProteins()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun syncExpenses() {
        expenseDao.getUnsynced().forEach { entity ->
            try {
                val response = expenseApi.createEntry(
                    CreateExpenseEntryRequest(
                        description = entity.title,
                        amount = entity.amount,
                        category = entity.category,
                        currency = entity.currency,
                        entryDate = entity.date
                    )
                )

                if (response.isSuccessful) {
                    val serverDto = response.body()
                    if (serverDto != null) {
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
                                createdAt = serverDto.entryDate,
                                updatedAt = serverDto.entryDate,
                                isSynced = true
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip for now
            }
        }
    }

    private suspend fun syncProteins() {
        proteinDao.getUnsynced().forEach { entity ->
            try {
                val response = proteinApi.createEntry(
                    CreateProteinEntryRequest(
                        foodName = entity.foodName,
                        gramsConsumed = entity.gramsConsumed,
                        proteinGrams = entity.proteinGrams,
                        entryDate = entity.date
                    )
                )

                if (response.isSuccessful) {
                    val serverDto = response.body()
                    if (serverDto != null) {
                        proteinDao.update(
                            entity.copy(
                                remoteId = serverDto.id,
                                isSynced = true
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip
            }
        }
    }

    companion object {
        const val WORK_NAME = "tracker_sync"

        fun schedule(workManager: WorkManager) {
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest()
            )
        }

        private fun periodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(
                1,
                TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        fun oneTimeRequest(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
    }
}