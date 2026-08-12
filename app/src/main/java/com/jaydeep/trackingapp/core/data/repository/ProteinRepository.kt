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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProteinRepository @Inject constructor(
    private val proteinDao: ProteinDao,
    private val proteinApi: ProteinApi,
    private val workManager: androidx.work.WorkManager,
) {

    fun getProteins(): Flow<List<ProteinEntity>> = proteinDao.getAll()

    suspend fun getProteinById(id: Long): ProteinEntity? = proteinDao.getById(id)

    suspend fun syncProteins(): Result<Unit> = withContext(Dispatchers.IO) {
        // Push local changes first
        com.jaydeep.trackingapp.core.sync.SyncWorker.enqueueNow(workManager)

        runCatching {
            val today = java.time.LocalDate.now()
            // Sync last 7 days
            for (i in 0..6) {
                val date = today.minusDays(i.toLong()).toString()
                val response = proteinApi.getEntries(date)
                if (response.isSuccessful) {
                    val serverEntries = response.body() ?: emptyList()
                    val serverRemoteIds = serverEntries.map { it.id }.toSet()

                    // Get local synced entries for this date
                    val localEntries = proteinDao.getAll().first().filter { 
                        it.date == date && it.isSynced 
                    }

                    // Delete local entries that are no longer on server
                    for (local in localEntries) {
                        if (local.remoteId != null && local.remoteId !in serverRemoteIds) {
                            proteinDao.deleteById(local.id)
                        }
                    }

                    // Insert/Update from server
                    proteinDao.insertAll(serverEntries.map { it.toEntity(isSynced = true) })
                }
            }
            Result.Success(Unit)
        }.getOrElse { 
            Result.Error(it.message ?: "Sync failed")
        }
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
                proteinDao.deleteById(localId)
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
        id: Long,
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
                id = id,
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

    suspend fun deleteProtein(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
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