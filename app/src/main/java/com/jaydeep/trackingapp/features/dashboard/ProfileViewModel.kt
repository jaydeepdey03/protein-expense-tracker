package com.jaydeep.trackingapp.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.repository.AuthRepository
import com.jaydeep.trackingapp.core.di.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    data class ProfileUIState(
        val name: String = "",
        val email: String = "",
        val proteinGoal: Float = 120f,
        val expenseBudget: Float = 25000f,
        val isLoggedOut: Boolean = false,
    )



//    val uiState = combine(
//        tokenStore.userName,
//        tokenStore.userEmail,
//        tokenStore.proteinGoal,
//        tokenStore.expenseBudget
//    ) { name, email, proteinGoal, expenseBudget ->
//        UiState(
//            name = name,
//            email = email,
//            proteinGoal = proteinGoal,
//            expenseBudget = expenseBudget
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = UiState()
//    )

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        // Start observing the TokenStore as soon as the ViewModel is created
        viewModelScope.launch {
            combine(
                tokenStore.userName,
                tokenStore.userEmail,
                tokenStore.proteinGoal,
                tokenStore.expenseBudget
            ) { name, email, proteinGoal, expenseBudget ->
                // This block runs every time any of these values change in the DataStore
                ProfileUIState(
                    name = name,
                    email = email,
                    proteinGoal = proteinGoal,
                    expenseBudget = expenseBudget,
                    isLoggedOut = _uiState.value.isLoggedOut // Preserve the current logout state
                )
            }.collect { newState ->
                // Update your MutableStateFlow with the new state
                _uiState.value = newState
            }
        }
    }

    fun updateProteinGoal(goal: Float) {
        viewModelScope.launch {
            tokenStore.saveProteinGoal(goal)
        }
    }

    fun updateExpenseBudget(budget: Float) {
        viewModelScope.launch {
            tokenStore.saveExpenseBudget(budget)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
