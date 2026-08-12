package com.jaydeep.trackingapp.features.auth.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.auth.AuthCancelledException
import com.jaydeep.trackingapp.core.auth.AuthCredential
import com.jaydeep.trackingapp.core.auth.GoogleAuthProvider
import com.jaydeep.trackingapp.features.auth.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    // GoogleAuthProvider injected here; swap/add providers trivially
    private val googleAuthProvider: GoogleAuthProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onGoogleSignInClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { googleAuthProvider.signIn() }
                .onSuccess { credential -> login(credential) }
                .onFailure { error ->
                    val message = if (error is AuthCancelledException) null
                    else error.message ?: "Sign-in failed"
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    private suspend fun login(credential: AuthCredential) {
        loginUseCase(credential)
            .onSuccess {
                _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Login failed")
                }
            }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}