package com.jaydeep.trackingapp.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.di.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenStore: TokenStore,
) : ViewModel() {

    data class UiState(
        val name: String = "",
        val email: String = "",
        val proteinGoal: Float = 120f,
        val expenseBudget: Float = 25000f
    )

    val uiState = combine(
        tokenStore.userName,
        tokenStore.userEmail,
        tokenStore.proteinGoal,
        tokenStore.expenseBudget
    ) { name, email, proteinGoal, expenseBudget ->
        UiState(
            name = name, 
            email = email,
            proteinGoal = proteinGoal,
            expenseBudget = expenseBudget
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

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
}
