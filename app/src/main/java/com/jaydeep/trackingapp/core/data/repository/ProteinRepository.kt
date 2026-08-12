package com.jaydeep.trackingapp.core.data.repository

import com.jaydeep.trackingapp.core.data.local.dao.ProteinDao
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import com.jaydeep.trackingapp.core.data.remote.api.ProteinApi
import com.jaydeep.trackingapp.core.data.remote.dto.CreateProteinRequest
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinDto
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateProteinRequest
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

    suspend fun syncProteins(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = proteinApi.getProteins()
            if (response.isSuccessful) {
                val remote = response.body() ?: emptyList()
                proteinDao.insertAll(remote.map { it.toEntity(isSynced = true) })
            } else {
                error("Sync failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Sync failed") },
        )
    }

    suspend fun createProtein(
        foodName: String,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // Insert optimistically with a local UUID — survives offline
        val local = ProteinEntity(
            foodName = foodName,
            proteinGrams = proteinGrams,
            calories = calories,
            note = note,
            date = date,
            isSynced = false,
        )
        proteinDao.insert(local)

        runCatching {
            val response = proteinApi.createProtein(
                CreateProteinRequest(
                    foodName = foodName,
                    proteinGrams = proteinGrams,
                    calories = calories,
                    note = note,
                    date = date,
                )
            )
            if (response.isSuccessful) {
                // Drop the local UUID record, replace with server-assigned String id
                proteinDao.deleteById(local.id.toString())
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
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        proteinDao.update(
            ProteinEntity(
                id = id.toLong(),
                foodName = foodName,
                proteinGrams = proteinGrams,
                calories = calories,
                note = note,
                date = date,
                isSynced = false,
            )
        )

        runCatching {
            val response = proteinApi.updateProtein(
                id = id,
                request = UpdateProteinRequest(
                    foodName = foodName,
                    proteinGrams = proteinGrams,
                    calories = calories,
                    note = note,
                    date = date,
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
        proteinDao.deleteById(id)

        runCatching {
            val response = proteinApi.deleteProtein(id)
            if (!response.isSuccessful) {
                error("Delete failed: ${response.code()}")
            }
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { Result.Error(it.message ?: "Delete failed") },
        )
    }

    // --- Mappers ---

    private fun ProteinDto.toEntity(isSynced: Boolean) = ProteinEntity(
        id = id.toLong(),
        remoteId = id,
        foodName = foodName,
        proteinGrams = proteinGrams,
        calories = calories,
        note = note,
        date = date,
        isSynced = isSynced,
    )
}