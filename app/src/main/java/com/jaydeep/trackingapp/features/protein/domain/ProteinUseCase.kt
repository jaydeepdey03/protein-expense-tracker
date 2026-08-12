package com.jaydeep.trackingapp.features.protein.domain

import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.util.Result
import javax.inject.Inject

class ProteinUseCase @Inject constructor(
    private val repository: ProteinRepository,
) {

    suspend fun createProtein(
        foodName: String,
        gramsConsumed: Double,
        proteinGrams: Double,
        calories: Int?,
        note: String?,
        date: String,
    ): Result<Unit> {
        if (foodName.isBlank()) return Result.Error("Food name cannot be empty")
        if (gramsConsumed <= 0) return Result.Error("Grams consumed must be greater than 0")
        if (proteinGrams <= 0) return Result.Error("Protein must be greater than 0")
        if (calories != null && calories < 0) return Result.Error("Calories cannot be negative")
        if (date.isBlank()) return Result.Error("Date cannot be empty")

        return repository.createProtein(
            foodName = foodName.trim(),
            gramsConsumed = gramsConsumed,
            proteinGrams = proteinGrams,
            calories = calories,
            note = note?.trim()?.ifEmpty { null },
            date = date,
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
    ): Result<Unit> {
        if (foodName.isBlank()) return Result.Error("Food name cannot be empty")
        if (gramsConsumed <= 0) return Result.Error("Grams consumed must be greater than 0")
        if (proteinGrams <= 0) return Result.Error("Protein must be greater than 0")
        if (calories != null && calories < 0) return Result.Error("Calories cannot be negative")
        if (date.isBlank()) return Result.Error("Date cannot be empty")

        return repository.updateProtein(
            id = id,
            foodName = foodName.trim(),
            gramsConsumed = gramsConsumed,
            proteinGrams = proteinGrams,
            calories = calories,
            note = note?.trim()?.ifEmpty { null },
            date = date,
        )
    }

    suspend fun deleteProtein(id: String): Result<Unit> =
        repository.deleteProtein(id)
}