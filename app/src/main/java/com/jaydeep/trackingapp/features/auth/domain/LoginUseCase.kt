package com.jaydeep.trackingapp.features.auth.domain

import com.jaydeep.trackingapp.core.auth.AuthCredential
import com.jaydeep.trackingapp.core.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(credential: AuthCredential): Result<Unit> {
        // Validate per credential type before hitting the network
        when (credential) {
            is AuthCredential.Google -> {
                require(credential.idToken.isNotBlank()) { "Google ID token is empty" }
            }
            is AuthCredential.EmailPassword -> {
                require(credential.email.isNotBlank()) { "Email is required" }
                require(credential.password.length >= 8) { "Password must be at least 8 characters" }
            }
        }
        return repository.login(credential)
    }
}