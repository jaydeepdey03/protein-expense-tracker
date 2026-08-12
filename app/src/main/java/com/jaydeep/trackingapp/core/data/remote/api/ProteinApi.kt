package com.jaydeep.trackingapp.core.data.remote.api

import com.jaydeep.trackingapp.core.data.remote.dto.CreateProteinEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.DailyProteinSummaryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateProteinEntryRequest
import retrofit2.Response
import retrofit2.http.*

interface ProteinApi {

    @POST("api/protein/entries")
    suspend fun createEntry(@Body request: CreateProteinEntryRequest): Response<ProteinEntryResponse>

    @PUT("api/protein/entries/{entryId}")
    suspend fun updateEntry(
        @Path("entryId") entryId: String,
        @Body request: UpdateProteinEntryRequest
    ): Response<ProteinEntryResponse>

    @DELETE("api/protein/entries/{entryId}")
    suspend fun deleteEntry(@Path("entryId") entryId: String): Response<Unit>

    @GET("api/protein/entries")
    suspend fun getEntries(@Query("date") date: String): Response<List<ProteinEntryResponse>>

    @GET("api/protein/summary")
    suspend fun getSummary(@Query("date") date: String): Response<DailyProteinSummaryResponse>
}
