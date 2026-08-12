package com.jaydeep.trackingapp.core.data.remote.api

import com.jaydeep.trackingapp.core.data.remote.dto.AuthResponse
import com.jaydeep.trackingapp.core.data.remote.dto.GoogleLoginRequest
import com.jaydeep.trackingapp.core.data.remote.dto.LogoutRequest
import com.jaydeep.trackingapp.core.data.remote.dto.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest)
}