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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val userEmail: String = "",
    val today: String = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
    // Goals
    val dailyProteinGoal: Float = 120f,
    val monthlyExpenseBudget: Float = 25000f,
    // Progress values
    val todayProteinTotal: Double = 0.0,
    val monthlyExpenseTotal: Double = 0.0,
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
        syncData()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            expenseRepository.syncExpenses()
            proteinRepository.syncProteins()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            expenseRepository.syncExpenses()
            proteinRepository.syncProteins()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = tokenStore.getUser()
            _uiState.update {
                it.copy(
                    userName = user?.name ?: "User",
                    userEmail = user?.email ?: "",
                )
            }
        }
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
                val currentMonth = today.monthValue
                val currentYear = today.year

                val todayProteins = proteins.filter { it.date == todayStr }
                val monthlyExpenses = expenses.filter {
                    val date = try {
                        LocalDate.parse(it.date)
                    } catch (e: Exception) {
                        null
                    }
                    date?.monthValue == currentMonth && date?.year == currentYear
                }

                _uiState.update {
                    it.copy(
                        dailyProteinGoal = proteinGoal,
                        monthlyExpenseBudget = expenseBudget,
                        todayProteinTotal = todayProteins.sumOf { p -> p.proteinGrams },
                        monthlyExpenseTotal = monthlyExpenses.sumOf { e -> e.amount },
                        recentExpenses = expenses.sortedByDescending { e -> e.date }.take(10).map { e ->
                            RecentExpenseItem(
                                id = e.id,
                                title = e.title,
                                category = e.category,
                                amount = e.amount,
                                currency = e.currency,
                                date = e.date,
                            )
                        },
                        recentProteins = proteins.sortedByDescending { p -> p.date }.take(10).map { p ->
                            RecentProteinItem(
                                id = p.id,
                                foodName = p.foodName,
                                proteinGrams = p.proteinGrams,
                                date = p.date,
                            )
                        },
                    )
                }
            }.collect {}
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clear()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }

    fun deleteProtein(id: Long) {
        viewModelScope.launch {
            proteinRepository.deleteProtein(id)
        }
    }
}
