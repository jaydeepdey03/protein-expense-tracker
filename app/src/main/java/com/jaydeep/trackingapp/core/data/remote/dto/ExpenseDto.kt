package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseEntryResponse(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "description") val description: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "currency") val currency: String,
    @param:Json(name = "entryDate") val entryDate: String
)

@JsonClass(generateAdapter = true)
data class CreateExpenseEntryRequest(
    @param:Json(name = "description") val description: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "currency") val currency: String = "INR",
    @param:Json(name = "entryDate") val entryDate: String
)

@JsonClass(generateAdapter = true)
data class UpdateExpenseEntryRequest(
    @param:Json(name = "amount") val amount: Double
)

@JsonClass(generateAdapter = true)
data class DailyExpenseSummaryResponse(
    @param:Json(name = "date") val date: String,
    @param:Json(name = "totalAmount") val totalAmount: Double,
    @param:Json(name = "entryCount") val entryCount: Int
)
