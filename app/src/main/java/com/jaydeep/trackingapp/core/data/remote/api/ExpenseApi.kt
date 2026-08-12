package com.jaydeep.trackingapp.core.data.remote.api

import com.jaydeep.trackingapp.core.data.remote.dto.CreateExpenseEntryRequest
import com.jaydeep.trackingapp.core.data.remote.dto.DailyExpenseSummaryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ExpenseEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateExpenseEntryRequest
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApi {

    @POST("api/expenses/entries")
    suspend fun createEntry(@Body request: CreateExpenseEntryRequest): Response<ExpenseEntryResponse>

    @PUT("api/expenses/entries/{entryId}")
    suspend fun updateEntry(
        @Path("entryId") entryId: String,
        @Body request: UpdateExpenseEntryRequest
    ): Response<ExpenseEntryResponse>

    @DELETE("api/expenses/entries/{entryId}")
    suspend fun deleteEntry(@Path("entryId") entryId: String): Response<Unit>

    @GET("api/expenses/entries")
    suspend fun getEntries(@Query("date") date: String): Response<List<ExpenseEntryResponse>>

    @GET("api/expenses/summary")
    suspend fun getSummary(@Query("date") date: String): Response<DailyExpenseSummaryResponse>
}
