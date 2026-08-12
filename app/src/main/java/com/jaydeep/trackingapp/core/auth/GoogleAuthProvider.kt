package com.jaydeep.trackingapp.core.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jaydeep.trackingapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleAuthProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AuthProvider {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(): AuthCredential {

        val authorizedAccountsRequest = buildGoogleCredentialRequest(
            filterByAuthorizedAccounts = true
        )

        val result = try {
            credentialManager.getCredential(
                context = context,
                request = authorizedAccountsRequest
            )

        } catch (e: NoCredentialException) {

            Log.d(
                TAG,
                "No authorized Google account found."
            )

            // No authorized account found.
            // Retry with all Google accounts.
            Log.d(
                TAG,
                "Retrying Google sign-in with ALL Google accounts"
            )

            val allAccountsRequest = buildGoogleCredentialRequest(
                filterByAuthorizedAccounts = false
            )

            try {
                credentialManager.getCredential(
                    context = context,
                    request = allAccountsRequest
                )
            } catch (e: GetCredentialCancellationException) {

                Log.d(
                    TAG,
                    "Google sign-in cancelled by user"
                )

                throw AuthCancelledException()

            } catch (e: NoCredentialException) {

                Log.e(
                    TAG,
                    "No Google credential found even after trying ALL accounts",
                    e
                )

                throw AuthFailedException(
                    "No Google account available on this device",
                    e
                )

            } catch (e: GetCredentialException) {

                Log.e(
                    TAG,
                    "Google sign-in failed on second attempt",
                    e
                )

                Log.e(
                    TAG,
                    "Credential exception type = ${e.type}"
                )

                throw AuthFailedException(
                    "Google sign-in failed: ${e.type}",
                    e
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Unexpected Google sign-in error on second attempt",
                    e
                )

                throw AuthFailedException(
                    e.message ?: "Google sign-in failed",
                    e
                )
            }

        } catch (e: GetCredentialCancellationException) {

            Log.d(
                TAG,
                "Google sign-in cancelled by user"
            )

            throw AuthCancelledException()

        } catch (e: GetCredentialException) {

            Log.e(
                TAG,
                "Google sign-in failed on first attempt",
                e
            )

            Log.e(
                TAG,
                "Credential exception type = ${e.type}"
            )

            throw AuthFailedException(
                "Google sign-in failed: ${e.type}",
                e
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Unexpected Google sign-in error",
                e
            )

            throw AuthFailedException(
                e.message ?: "Google sign-in failed",
                e
            )
        }

        Log.d(
            TAG,
            "Google credential received successfully"
        )

        val credential = result.credential

        Log.d(
            TAG,
            "Credential type = ${credential.type}"
        )

        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            Log.e(
                TAG,
                "Unexpected credential type: ${credential.type}"
            )

            throw AuthFailedException(
                "Unsupported credential type from Google"
            )
        }

        val googleCredential = try {

            Log.d(
                TAG,
                "Parsing Google ID token credential"
            )

            GoogleIdTokenCredential.createFrom(
                credential.data
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to parse Google credential",
                e
            )

            throw AuthFailedException(
                "Could not parse Google credentials",
                e
            )
        }

        Log.d(
            TAG,
            "Google ID token credential parsed successfully"
        )

        // IMPORTANT:
        // Do NOT log googleCredential.idToken here.
        Log.d(
            TAG,
            "========== Google Sign-In Successful =========="
        )

        return AuthCredential.Google(
            idToken = googleCredential.idToken
        )
    }

    private fun buildGoogleCredentialRequest(
        filterByAuthorizedAccounts: Boolean,
    ): GetCredentialRequest {

        Log.d(
            TAG,
            "Building Google credential request"
        )

        Log.d(
            TAG,
            "filterByAuthorizedAccounts = $filterByAuthorizedAccounts"
        )

        Log.d(
            TAG,
            "Server Client ID = ${BuildConfig.GOOGLE_CLIENT_ID}"
        )

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(
                filterByAuthorizedAccounts
            )
            .setServerClientId(
                BuildConfig.GOOGLE_CLIENT_ID
            )
            .setAutoSelectEnabled(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun signOut() {

        Log.d(
            TAG,
            "Clearing Google credential state"
        )

        try {
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )

            Log.d(
                TAG,
                "Google credential state cleared successfully"
            )

        } catch (e: Exception) {

            Log.w(
                TAG,
                "Failed to clear Google credential state",
                e
            )
        }
    }

    companion object {
        private const val TAG = "GoogleAuth"
    }
}

class AuthCancelledException :
    Exception("Sign-in cancelled by user")

class AuthFailedException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)