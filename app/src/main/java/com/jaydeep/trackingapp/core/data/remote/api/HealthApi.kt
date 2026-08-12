package com.jaydeep.trackingapp.core.data.remote.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class HealthResponse(
    @param:Json(name = "status") val status: String
)

interface HealthApi {
    @GET("actuator/health")
    suspend fun getHealth(): HealthResponse
}