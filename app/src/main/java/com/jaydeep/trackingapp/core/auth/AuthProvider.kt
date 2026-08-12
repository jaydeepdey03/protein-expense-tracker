package com.jaydeep.trackingapp.core.auth

sealed interface AuthCredential {
    data class Google(val idToken: String) : AuthCredential
    data class EmailPassword(val email: String, val password: String) : AuthCredential
    // Future: data class Apple(val identityToken: String) : AuthCredential
}

interface AuthProvider {
    /**
     * Launch the provider's sign-in flow and return a credential,
     * or throw [AuthCancelledException] / [AuthFailedException] on failure.
     */
    suspend fun signIn(): AuthCredential
}