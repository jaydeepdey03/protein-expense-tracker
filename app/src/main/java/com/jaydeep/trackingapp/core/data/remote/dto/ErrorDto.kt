package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorDto(
    @param:Json(name = "timestamp") val timestamp: String,
    @param:Json(name = "status") val status: Int,
    @param:Json(name = "message") val message: String
)
