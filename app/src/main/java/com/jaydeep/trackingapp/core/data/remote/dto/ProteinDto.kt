package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProteinDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "food_name") val foodName: String,
    @param:Json(name = "protein_grams") val proteinGrams: Double,
    @param:Json(name = "calories") val calories: Int?,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,
    @param:Json(name = "created_at") val createdAt: String,
    @param:Json(name = "updated_at") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
data class CreateProteinRequest(
    @param:Json(name = "food_name") val foodName: String,
    @param:Json(name = "protein_grams") val proteinGrams: Double,
    @param:Json(name = "calories") val calories: Int?,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,
)

@JsonClass(generateAdapter = true)
data class UpdateProteinRequest(
    @param:Json(name = "food_name") val foodName: String,
    @param:Json(name = "protein_grams") val proteinGrams: Double,
    @param:Json(name = "calories") val calories: Int?,
    @param:Json(name = "note") val note: String?,
    @param:Json(name = "date") val date: String,
)