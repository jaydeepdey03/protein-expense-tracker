package com.jaydeep.trackingapp.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.di.TokenStore
import com.jaydeep.trackingapp.core.ui.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val startDestination: String) : AuthState()
    data class Unauthenticated(val startDestination: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            if (tokenStore.hasValidToken()) {
                _authState.value = AuthState.Authenticated(Screens.Dashboard.route)
            } else {
                _authState.value = AuthState.Unauthenticated(Screens.Login.route)
            }
        }
    }
}
