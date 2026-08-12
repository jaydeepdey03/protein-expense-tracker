package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProteinEntryResponse(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "foodName") val foodName: String,
    @param:Json(name = "gramsConsumed") val gramsConsumed: Double,
    @param:Json(name = "proteinGrams") val proteinGrams: Double,
    @param:Json(name = "entryDate") val entryDate: String
)

@JsonClass(generateAdapter = true)
data class CreateProteinEntryRequest(
    @param:Json(name = "foodName") val foodName: String,
    @param:Json(name = "gramsConsumed") val gramsConsumed: Double,
    @param:Json(name = "proteinGrams") val proteinGrams: Double,
    @param:Json(name = "entryDate") val entryDate: String
)

@JsonClass(generateAdapter = true)
data class UpdateProteinEntryRequest(
    @param:Json(name = "proteinGrams") val proteinGrams: Double
)

@JsonClass(generateAdapter = true)
data class DailyProteinSummaryResponse(
    @param:Json(name = "date") val date: String,
    @param:Json(name = "totalProteinGrams") val totalProteinGrams: Double,
    @param:Json(name = "entryCount") val entryCount: Int
)
