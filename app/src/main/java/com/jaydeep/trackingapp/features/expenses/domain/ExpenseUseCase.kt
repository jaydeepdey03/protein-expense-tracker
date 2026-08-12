package com.jaydeep.trackingapp.features.expenses.domain

import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.util.Result
import javax.inject.Inject

class ExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
) {

    suspend fun createExpense(
        description: String,
        category: String,
        amount: Double,
        currency: String,
        entryDate: String,
    ): Result<Unit> {
        if (description.isBlank()) return Result.Error("Description cannot be empty")
        if (category.isBlank()) return Result.Error("Category cannot be empty")
        if (amount < 0) return Result.Error("Amount cannot be negative")
        if (entryDate.isBlank()) return Result.Error("Date cannot be empty")

        return repository.createExpense(
            description = description.trim(),
            category = category.trim(),
            amount = amount,
            currency = currency.ifBlank { "INR" },
            entryDate = entryDate,
        )
    }

    suspend fun updateExpense(
        id: String,
        description: String,
        category: String,
        amount: Double,
        currency: String,
        entryDate: String,
    ): Result<Unit> {
        if (description.isBlank()) return Result.Error("Description cannot be empty")
        if (category.isBlank()) return Result.Error("Category cannot be empty")
        if (amount < 0) return Result.Error("Amount cannot be negative")
        if (entryDate.isBlank()) return Result.Error("Date cannot be empty")

        return repository.updateExpense(
            id = id,
            description = description.trim(),
            category = category.trim(),
            amount = amount,
            currency = currency.ifBlank { "INR" },
            entryDate = entryDate,
        )
    }

    suspend fun deleteExpense(id: String): Result<Unit> =
        repository.deleteExpense(id)
}