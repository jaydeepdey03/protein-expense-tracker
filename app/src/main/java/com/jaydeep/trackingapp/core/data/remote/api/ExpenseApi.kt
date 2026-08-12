package com.jaydeep.trackingapp.core.data.remote.api

import com.jaydeep.trackingapp.core.data.remote.dto.CreateExpenseRequest
import com.jaydeep.trackingapp.core.data.remote.dto.ExpenseDto
import com.jaydeep.trackingapp.core.data.remote.dto.UpdateExpenseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExpenseApi {

    @GET("expenses")
    suspend fun getExpenses(): Response<List<ExpenseDto>>

    @GET("expenses/{id}")
    suspend fun getExpense(@Path("id") id: Long): Response<ExpenseDto>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<ExpenseDto>

    @PUT("expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: Long,
        @Body request: UpdateExpenseRequest,
    ): Response<ExpenseDto>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit>
}
