package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseDto(
    @param:Json(name = "id") val id: Long,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,           // ISO-8601 date string
    @param:Json(name = "created_at") val createdAt: String,
    @param:Json(name = "updated_at") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
data class CreateExpenseRequest(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,
)

@JsonClass(generateAdapter = true)
data class UpdateExpenseRequest(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,
)