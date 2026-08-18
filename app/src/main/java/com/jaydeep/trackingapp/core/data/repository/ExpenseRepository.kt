package com.jaydeep.trackingapp.core.data.repository

import com.jaydeep.trackingapp.core.data.local.dao.ExpenseDao
import com.jaydeep.trackingapp.core.data.local.entities.ExpenseEntity
import com.jaydeep.trackingapp.core.data.remote.api.ExpenseApi
import com.jaydeep.trackingapp.core.data.remote.dto.CreateExpenseEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.DailyExpenseSummaryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ExpenseEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateExpenseEntryRequest
import com.jaydeep.trackingapp.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseApi: ExpenseApi,
    private val tokenStore: com.jaydeep.trackingapp.core.di.TokenStore,
) {

    fun getExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAll()

    suspend fun getExpenseById(id: String): ExpenseEntity? = expenseDao.getById(id)

    suspend fun syncExpenses(): Result<Unit> = withContext(Dispatchers.IO) {

        runCatching {
            val today = java.time.LocalDate.now()
            // Sync last 7 days to catch deletions on other devices or previous sync failures
            for (i in 0..6) {
                val date = today.minusDays(i.toLong()).toString()
                val response = expenseApi.getEntries(date)
                if (response.isSuccessful) {
                    val serverEntries = response.body() ?: emptyList()
                    val serverIds = serverEntries.map { it.id }.toSet()

                    // Get local synced entries for this date
                    val localEntries = expenseDao.getSyncedByDate(date)

                    // Delete local entries that are no longer on server
                    for (local in localEntries) {
                        if (local.id !in serverIds) {
                            expenseDao.deleteById(local.id)
                        }
                    }

                    // Insert/Update from server
                    expenseDao.insertAll(serverEntries.map { it.toEntity(isSynced = true) })
                }
            }
            Result.Success(Unit)
        }.getOrElse { 
            Result.Error(it.message ?: "Sync failed")
        }
    }

    suspend fun createExpense(
        description: String,
        category: String,
        amount: Double,
        currency: String,
        entryDate: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val local = ExpenseEntity(
            id = UUID.randomUUID().toString(),
            userId = tokenStore.getUser()?.id ?: "",
            title = description,
            amount = amount,
            currency = currency,
            date = entryDate,
            category = category,
            notes = null,
            createdAt = entryDate,
            updatedAt = entryDate,
            isSynced = false
        )
        expenseDao.insert(local)

        runCatching {
            val response = expenseApi.createEntry(
                CreateExpenseEntryRequest(
                    description = description,
                    amount = amount,
                    category = category,
                    currency = currency,
                    entryDate = entryDate,
                )
            )
            if (response.isSuccessful) {
                expenseDao.deleteById(local.id)
                expenseDao.insert(response.body()!!.toEntity(isSynced = true))
            }
        }
        
        return@withContext Result.Success(Unit)
    }

    suspend fun updateExpense(
        id: String,
        description: String,
        category: String,
        amount: Double,
        currency: String,
        entryDate: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = expenseDao.getById(id)
        val createdAt = existing?.createdAt ?: entryDate

        expenseDao.update(
            ExpenseEntity(
                id = id,
                userId = tokenStore.getUser()?.id ?: "",
                title = description,
                amount = amount,
                currency = currency,
                date = entryDate,
                category = category,
                notes = null,
                createdAt = createdAt,
                updatedAt = entryDate,
                isSynced = false,
            )
        )

        // If not synced yet, the ID is a local UUID.
        // SyncWorker will handle creating it on the server.
        if (existing?.isSynced != true) {
            return@withContext Result.Success(Unit)
        }

        runCatching {
            val response = expenseApi.updateEntry(
                entryId = id,
                request = UpdateExpenseEntryRequest(
                    amount = amount
                )
            )
            if (response.isSuccessful) {
                expenseDao.markSynced(id)
            } else {
                error("Update failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Update failed") },
        )
    }

    suspend fun deleteExpense(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = expenseDao.getById(id)
        expenseDao.deleteById(id)

        // If not synced yet, no need to call remote delete.
        if (existing?.isSynced != true) {
            return@withContext Result.Success(Unit)
        }

        runCatching {
            val response = expenseApi.deleteEntry(id)
            if (!response.isSuccessful) {
                error("Delete failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Delete failed") },
        )
    }

    suspend fun getDailyExpenseSummary(date: String): Result<DailyExpenseSummaryResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = expenseApi.getSummary(date)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.Success(body)
                else Result.Error("Empty response body")
            } else {
                Result.Error("Failed to fetch summary: ${response.code()}")
            }
        }.getOrElse {
            Result.Error(it.message ?: "Unknown error")
        }
    }

    suspend fun getDailyExpenseEntries(date: String): Result<List<ExpenseEntryResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = expenseApi.getEntries(date)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.Success(body)
                else Result.Error("Empty response body")
            } else {
                Result.Error("Failed to fetch entries: ${response.code()}")
            }
        }.getOrElse {
            Result.Error(it.message ?: "Unknown error")
        }
    }

    private suspend fun ExpenseEntryResponse.toEntity(isSynced: Boolean) = ExpenseEntity(
        id = id,
        userId = tokenStore.getUser()?.id ?: "",
        title = description,
        amount = amount,
        currency = currency,
        date = entryDate,
        category = category,
        notes = null,
        createdAt = entryDate,
        updatedAt = entryDate,
        isSynced = isSynced,
    )

    suspend fun pushUnsyncedExpenses() {
        val unsyncedExpenses = expenseDao.getUnsynced()
        if (unsyncedExpenses.isEmpty()) return
        unsyncedExpenses.forEach { entity ->
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
                        expenseDao.markSynced(entity.id, serverDto.id)
                    }
                }
            } catch (e: Exception) {
                // Let worker retry
            }
        }
    }
}