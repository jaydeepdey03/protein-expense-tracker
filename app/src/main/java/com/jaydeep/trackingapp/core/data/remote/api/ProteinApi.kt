package com.jaydeep.trackingapp.core.data.remote.api

import com.jaydeep.trackingapp.core.data.remote.dto.CreateProteinRequest
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinDto
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateProteinRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ProteinApi {

    @GET("proteins")
    suspend fun getProteins(): Response<List<ProteinDto>>

    @GET("proteins/{id}")
    suspend fun getProtein(@Path("id") id: String): Response<ProteinDto>

    @POST("proteins")
    suspend fun createProtein(@Body request: CreateProteinRequest): Response<ProteinDto>

    @PUT("proteins/{id}")
    suspend fun updateProtein(
        @Path("id") id: String,
        @Body request: UpdateProteinRequest,
    ): Response<ProteinDto>

    @DELETE("proteins/{id}")
    suspend fun deleteProtein(@Path("id") id: String): Response<Unit>
}