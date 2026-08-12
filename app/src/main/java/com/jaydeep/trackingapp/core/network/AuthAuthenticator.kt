package com.jaydeep.trackingapp.core.network

import com.jaydeep.trackingapp.core.data.remote.api.AuthApi
import com.jaydeep.trackingapp.core.data.remote.dto.RefreshRequest
import com.jaydeep.trackingapp.core.di.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class AuthAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApi: Provider<AuthApi>,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.retryCount > 1) return null

        val newToken = runBlocking {
            try {
                val refreshToken = tokenStore.refreshToken() ?: return@runBlocking null
                val refreshed = authApi.get().refresh(RefreshRequest(refreshToken))
                tokenStore.saveTokens(refreshed.accessToken, refreshed.refreshToken)
                refreshed.accessToken
            } catch (e: Exception) {
                tokenStore.clear()
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }

    private val Response.retryCount: Int
        get() {
            var count = 0
            var prior = priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }

}