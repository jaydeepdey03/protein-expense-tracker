package com.jaydeep.trackingapp.core.data.repository

import com.jaydeep.trackingapp.core.data.local.dao.ProteinDao
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import com.jaydeep.trackingapp.core.data.remote.api.ProteinApi
import com.jaydeep.trackingapp.core.data.remote.dto.CreateProteinEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateProteinEntryRequest
import com.jaydeep.trackingapp.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProteinRepository @Inject constructor(
    private val proteinDao: ProteinDao,
    private val proteinApi: ProteinApi,
) {

    fun getProteins(): Flow<List<ProteinEntity>> = proteinDao.getAll()

    suspend fun getProteinById(id: String): ProteinEntity? = proteinDao.getById(id)

    suspend fun syncProteins(): Result<Unit> = withContext(Dispatchers.IO) {
        // Full sync is no longer supported by the API.
        // Syncing individual unsynced entries is handled by SyncWorker.
        Result.Success(Unit)
    }

    suspend fun createProtein(
        foodName: String,
        gramsConsumed: Double,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // Insert optimistically with a local id — survives offline
        val local = ProteinEntity(
            foodName = foodName,
            gramsConsumed = gramsConsumed,
            proteinGrams = proteinGrams,
            calories = calories,
            note = note,
            date = date,
            isSynced = false,
        )
        val localId = proteinDao.insert(local)

        runCatching {
            val response = proteinApi.createEntry(
                CreateProteinEntryRequest(
                    foodName = foodName,
                    gramsConsumed = gramsConsumed,
                    proteinGrams = proteinGrams,
                    entryDate = date,
                )
            )
            if (response.isSuccessful) {
                // Drop the local record, replace with server-assigned String id (remoteId)
                proteinDao.deleteById(localId.toString())
                proteinDao.insert(response.body()!!.toEntity(isSynced = true))
            } else {
                error("Create failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Create failed") },
        )
    }

    suspend fun updateProtein(
        id: String,
        foodName: String,
        gramsConsumed: Double,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = proteinDao.getById(id)
        
        proteinDao.update(
            ProteinEntity(
                id = id.toLong(),
                remoteId = existing?.remoteId,
                foodName = foodName,
                gramsConsumed = gramsConsumed,
                proteinGrams = proteinGrams,
                calories = calories,
                note = note,
                date = date,
                isSynced = false,
            )
        )

        val remoteId = existing?.remoteId ?: return@withContext Result.Success(Unit)

        runCatching {
            val response = proteinApi.updateEntry(
                entryId = remoteId,
                request = UpdateProteinEntryRequest(
                    proteinGrams = proteinGrams,
                )
            )
            if (response.isSuccessful) {
                proteinDao.markSynced(id)
            } else {
                error("Update failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Update failed") },
        )
    }

    suspend fun deleteProtein(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = proteinDao.getById(id)
        proteinDao.deleteById(id)

        val remoteId = existing?.remoteId ?: return@withContext Result.Success(Unit)

        runCatching {
            val response = proteinApi.deleteEntry(remoteId)
            if (!response.isSuccessful) {
                error("Delete failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Delete failed") },
        )
    }

    // --- Mappers ---

    private fun ProteinEntryResponse.toEntity(isSynced: Boolean) = ProteinEntity(
        remoteId = id,
        foodName = foodName,
        gramsConsumed = gramsConsumed,
        proteinGrams = proteinGrams,
        date = entryDate,
        isSynced = isSynced,
    )
}