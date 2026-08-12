package com.jaydeep.trackingapp.core.data.repository

import com.jaydeep.trackingapp.core.auth.AuthCredential
import com.jaydeep.trackingapp.core.data.remote.api.AuthApi
import com.jaydeep.trackingapp.core.data.remote.dto.GoogleLoginRequest
import com.jaydeep.trackingapp.core.data.remote.dto.LogoutRequest
import com.jaydeep.trackingapp.core.data.remote.dto.RefreshRequest
import com.jaydeep.trackingapp.core.di.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
) {
    /**
     * Exchange any [AuthCredential] for backend tokens.
     * Add new credential types here as the backend supports them.
     */
    suspend fun login(credential: AuthCredential): Result<Unit> =
        runCatching {
            val response = when (credential) {
                is AuthCredential.Google ->
                    api.loginWithGoogle(GoogleLoginRequest(credential.idToken, "GOOGLE"))

                is AuthCredential.EmailPassword ->
                    error("Email/password login not yet supported by backend")
            }
            tokenStore.saveTokens(response.accessToken, response.refreshToken)
            // your saveUser takes 3 params, not a UserDto
            response.user?.let {
                tokenStore.saveUser(it.id, it.name, it.email)
            }
        }

    suspend fun refresh(): Result<Unit> =
        runCatching {
            val refreshToken = tokenStore.refreshToken()
                ?: error("No refresh token stored")
            val response = api.refresh(RefreshRequest(refreshToken))
            tokenStore.saveTokens(response.accessToken, response.refreshToken)
        }

    suspend fun logout(): Result<Unit> =
        runCatching {
            val refreshToken = tokenStore.refreshToken()
            if (refreshToken != null) {
                runCatching { api.logout(LogoutRequest(refreshToken)) }
                // Best-effort — clear tokens regardless of network result
            }
            tokenStore.clear()
        }
}