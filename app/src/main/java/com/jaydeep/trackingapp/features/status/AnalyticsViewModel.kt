package com.jaydeep.trackingapp.features.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AnalyticsUiState(
    val weeklyExpenses: List<Double> = emptyList(),
    val weeklyProteins: List<Double> = emptyList(),
    val labels: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val proteinRepository: ProteinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val today = LocalDate.now()
            val dates = (0..6).map { today.minusDays(it.toLong()).toString() }.reversed()
            val labels = dates.map { LocalDate.parse(it).dayOfWeek.name.take(3) }

            val expenseDeferred = dates.map { date ->
                async { expenseRepository.getDailyExpenseSummary(date) }
            }
            val proteinDeferred = dates.map { date ->
                async { proteinRepository.getDailyProteinSummary(date) }
            }

            val expenseResults = expenseDeferred.awaitAll()
            val proteinResults = proteinDeferred.awaitAll()

            val expenses = expenseResults.map { result ->
                if (result is Result.Success) result.data.totalAmount else 0.0
            }
            
            val proteins = proteinResults.map { result ->
                if (result is Result.Success) result.data.totalProteinGrams else 0.0
            }

            _uiState.update { 
                it.copy(
                    weeklyExpenses = expenses,
                    weeklyProteins = proteins,
                    labels = labels,
                    isLoading = false
                )
            }
        }
    }
}
