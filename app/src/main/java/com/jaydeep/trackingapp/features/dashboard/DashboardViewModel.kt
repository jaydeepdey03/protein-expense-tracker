package com.jaydeep.trackingapp.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.remote.api.HealthApi
import com.jaydeep.trackingapp.core.data.remote.dto.UserDto
import com.jaydeep.trackingapp.core.data.repository.AuthRepository
import com.jaydeep.trackingapp.core.di.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val user: UserDto? = null,
    val accessTokenPreview: String? = null,
    val refreshTokenPreview: String? = null,
    val hasValidToken: Boolean = false,
    val backendReachable: Boolean = false,
    val backendStatus: String = "Checking…",
    val googleIdTokenReceived: Boolean = false,
    val googleEmailVerified: Boolean = false,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
    private val healthApi: HealthApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Run token/user load and health check concurrently
            val userDeferred         = async { tokenStore.getUser() }
            val accessDeferred       = async { tokenStore.accessToken() }
            val refreshDeferred      = async { tokenStore.refreshToken() }
            val hasValidDeferred     = async { tokenStore.hasValidToken() }
            val healthDeferred       = async { checkHealth() }

            val user         = userDeferred.await()
            val accessToken  = accessDeferred.await()
            val refreshToken = refreshDeferred.await()
            val hasValid     = hasValidDeferred.await()
            val (reachable, statusText) = healthDeferred.await()

            _uiState.update {
                it.copy(
                    isLoading            = false,
                    user                 = user,
                    accessTokenPreview   = accessToken?.take(24)?.plus("…"),
                    refreshTokenPreview  = refreshToken?.take(24)?.plus("…"),
                    hasValidToken        = hasValid,
                    backendReachable     = reachable,
                    backendStatus        = statusText,
                    googleEmailVerified  = user?.email?.isNotBlank() == true,
                )
            }
        }
    }

    private suspend fun checkHealth(): Pair<Boolean, String> {
        return try {
            val response = healthApi.getHealth()
            val up = response.status.equals("UP", ignoreCase = true)
            Pair(up, response.status)
        } catch (e: Exception) {
            Pair(false, e.message?.take(40) ?: "Error")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}