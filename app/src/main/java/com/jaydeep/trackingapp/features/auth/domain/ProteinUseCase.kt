package com.jaydeep.trackingapp.features.auth.domain

import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import javax.inject.Inject
import com.jaydeep.trackingapp.util.Result

class ProteinUseCase @Inject constructor(
    private val repository: ProteinRepository,
) {

    suspend fun createProtein(
        foodName: String,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> {
        if (foodName.isBlank()) return Result.Error("Food name cannot be empty")
        if (proteinGrams <= 0) return Result.Error("Protein must be greater than 0")
        if (calories != null && calories < 0) return Result.Error("Calories cannot be negative")
        if (date.isBlank()) return Result.Error("Date cannot be empty")

        return repository.createProtein(
            foodName = foodName.trim(),
            proteinGrams = proteinGrams,
            calories = calories,
            note = note?.trim()?.ifEmpty { null },
            date = date,
        )
    }

    suspend fun updateProtein(
        id: String,
        foodName: String,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> {
        if (foodName.isBlank()) return Result.Error("Food name cannot be empty")
        if (proteinGrams <= 0) return Result.Error("Protein must be greater than 0")
        if (calories != null && calories < 0) return Result.Error("Calories cannot be negative")
        if (date.isBlank()) return Result.Error("Date cannot be empty")

        return repository.updateProtein(
            id = id,
            foodName = foodName.trim(),
            proteinGrams = proteinGrams,
            calories = calories,
            note = note?.trim()?.ifEmpty { null },
            date = date,
        )
    }

    suspend fun deleteProtein(id: String): Result<Unit> =
        repository.deleteProtein(id)
}