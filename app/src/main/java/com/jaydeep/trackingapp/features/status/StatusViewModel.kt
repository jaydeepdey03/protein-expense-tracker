package com.jaydeep.trackingapp.features.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.core.di.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StatusUiState(
    val todayProteinConsumed: Double = 0.0,
    val proteinGoal: Double = 120.0,
    val monthlyExpenseTotal: Double = 0.0,
    val monthlyBudget: Double = 25000.0,
    val todayExpenseTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
)

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val proteinRepository: ProteinRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatusUiState())
    val uiState: StateFlow<StatusUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                expenseRepository.getExpenses(),
                proteinRepository.getProteins(),
                tokenStore.proteinGoal,
                tokenStore.expenseBudget
            ) { expenses, proteins, proteinGoal, expenseBudget ->
                val today = LocalDate.now()
                val todayStr = today.toString()
                
                val todayProteins = proteins.filter { it.date == todayStr }
                val todayExpenses = expenses.filter { it.date == todayStr }
                val monthlyExpenses = expenses.filter {
                    val date = LocalDate.parse(it.date)
                    date.monthValue == today.monthValue && date.year == today.year
                }

                StatusUiState(
                    todayProteinConsumed = todayProteins.sumOf { it.proteinGrams },
                    proteinGoal = proteinGoal.toDouble(),
                    monthlyExpenseTotal = monthlyExpenses.sumOf { it.amount },
                    monthlyBudget = expenseBudget.toDouble(),
                    todayExpenseTotal = todayExpenses.sumOf { it.amount },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clear()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
