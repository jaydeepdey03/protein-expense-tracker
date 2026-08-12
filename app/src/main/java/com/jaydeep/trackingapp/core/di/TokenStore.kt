package com.jaydeep.trackingapp.core.di

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jaydeep.trackingapp.core.data.remote.dto.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore(name = "tracker_tokens")

@Singleton
class TokenStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
){
    companion object {
        private val KEY_ACCESS_TOKEN  = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID       = stringPreferencesKey("user_id")
        private val KEY_USER_NAME     = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL    = stringPreferencesKey("user_email")
        private val KEY_THEME_MODE    = stringPreferencesKey("theme_mode")
        private val KEY_PROTEIN_GOAL  = stringPreferencesKey("protein_goal")
        private val KEY_EXPENSE_BUDGET = stringPreferencesKey("expense_budget")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { prefs->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getUser(): UserDto? {
        val prefs = context.tokenDataStore.data.firstOrNull() ?: return null
        val id    = prefs[KEY_USER_ID]    ?: return null
        val name  = prefs[KEY_USER_NAME]  ?: return null
        val email = prefs[KEY_USER_EMAIL] ?: return null
        return UserDto(id = id, name = name, email = email)
    }


    // ── Read (Flow) ───────────────────────────────────────────────────────────

    val accessTokenFlow: Flow<String?> = context.tokenDataStore.data
        .map { it[KEY_ACCESS_TOKEN] }

    val refreshTokenFlow: Flow<String?> = context.tokenDataStore.data
        .map { it[KEY_REFRESH_TOKEN] }

    val themeMode: Flow<String> = context.tokenDataStore.data
        .map { it[KEY_THEME_MODE] ?: "SYSTEM" }

    val userName: Flow<String> = context.tokenDataStore.data
        .map { it[KEY_USER_NAME] ?: "" }

    val userEmail: Flow<String> = context.tokenDataStore.data
        .map { it[KEY_USER_EMAIL] ?: "" }

    val proteinGoal: Flow<Float> = context.tokenDataStore.data
        .map { it[KEY_PROTEIN_GOAL]?.toFloatOrNull() ?: 120f }

    val expenseBudget: Flow<Float> = context.tokenDataStore.data
        .map { it[KEY_EXPENSE_BUDGET]?.toFloatOrNull() ?: 25000f }

    // ── Read (suspend — one-shot, for interceptors/authenticator) ─────────────

    suspend fun accessToken(): String?  =
        context.tokenDataStore.data.firstOrNull()?.get(KEY_ACCESS_TOKEN)

    suspend fun refreshToken(): String? =
        context.tokenDataStore.data.firstOrNull()?.get(KEY_REFRESH_TOKEN)

    suspend fun userName(): String? =
        context.tokenDataStore.data.firstOrNull()?.get(KEY_USER_NAME)

    suspend fun userEmail(): String? =
        context.tokenDataStore.data.firstOrNull()?.get(KEY_USER_EMAIL)

    // ── Validation ────────────────────────────────────────────────────────────

    suspend fun hasValidToken(): Boolean =
        accessToken()?.isNotBlank() == true

    // ── Clear ─────────────────────────────────────────────────────────────────

    suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
    }


    suspend fun saveUser(id: String, name: String, email: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_USER_ID]    = id
            prefs[KEY_USER_NAME]  = name
            prefs[KEY_USER_EMAIL] = email
        }
    }

    suspend fun saveThemeMode(mode: String) {
        context.tokenDataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun saveProteinGoal(goal: Float) {
        context.tokenDataStore.edit { it[KEY_PROTEIN_GOAL] = goal.toString() }
    }

    suspend fun saveExpenseBudget(budget: Float) {
        context.tokenDataStore.edit { it[KEY_EXPENSE_BUDGET] = budget.toString() }
    }
}
