package com.jaydeep.trackingapp.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GoogleLoginRequest(
    @param:Json(name = "token") val token: String,
    @param:Json(name = "provider") val provider: String
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @param:Json(name = "refreshToken") val refreshToken: String,
)

@JsonClass(generateAdapter = true)
data class LogoutRequest(
    @param:Json(name = "refresh_token") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @param:Json(name = "accessToken")  val accessToken: String,
    @param:Json(name = "refreshToken") val refreshToken: String,
    @param:Json(name = "expiresIn")    val expiresIn: Long? = null,
    @param:Json(name = "tokenType")    val tokenType: String? = null,
    @param:Json(name = "user")         val user: UserDto? = null,
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "displayName") val name: String,
    @param:Json(name = "email") val email: String,
)