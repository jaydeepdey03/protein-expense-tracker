package com.jaydeep.trackingapp.features.dashboard

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

data class DashboardUiState(
    val userName: String = "",
    val userEmail: String = "",
    val today: String = LocalDate.now().toString(),
    // Today'****pense summary
    val todayExpenseTotal: Double = 0.0,
    val todayExpenseCount: Int = 0,
    // Today's protein summary
    val todayProteinTotal: Double = 0.0,
    val todayProteinCount: Int = 0,
    // Recent entries
    val recentExpenses: List<RecentExpenseItem> = emptyList(),
    val recentProteins: List<RecentProteinItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
)

data class RecentExpenseItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: Double,
    val currency: String,
    val date: String,
)

data class RecentProteinItem(
    val id: Long,
    val foodName: String,
    val proteinGrams: Double,
    val date: String,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val proteinRepository: ProteinRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        observeData()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = tokenStore.getUser()
            _uiState.update {
                it.copy(
                    userName = user?.name ?: "",
                    userEmail = user?.email ?: "",
                )
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                expenseRepository.getExpenses(),
                proteinRepository.getProteins(),
            ) { expenses, proteins ->
                val today = LocalDate.now().toString()
                val todayExpenses = expenses.filter { it.date == today }
                val todayProteins = proteins.filter { it.date == today }

                _uiState.value.copy(
                    today = today,
                    todayExpenseTotal = todayExpenses.sumOf { it.amount },
                    todayExpenseCount = todayExpenses.size,
                    todayProteinTotal = todayProteins.sumOf { it.proteinGrams },
                    todayProteinCount = todayProteins.size,
                    recentExpenses = todayExpenses.take(3).map {
                        RecentExpenseItem(
                            id = it.id,
                            title = it.title,
                            category = it.category,
                            amount = it.amount,
                            currency = it.currency,
                            date = it.date,
                        )
                    },
                    recentProteins = todayProteins.take(3).map {
                        RecentProteinItem(
                            id = it.id,
                            foodName = it.foodName,
                            proteinGrams = it.proteinGrams,
                            date = it.date,
                        )
                    },
                    isLoading = false,
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