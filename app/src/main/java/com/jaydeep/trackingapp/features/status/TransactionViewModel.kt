package com.jaydeep.trackingapp.features.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.remote.dto.DailyExpenseSummaryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.DailyProteinSummaryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ExpenseEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinEntryResponse
import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val expenseEntries: List<ExpenseEntryResponse> = emptyList(),
    val proteinEntries: List<ProteinEntryResponse> = emptyList(),
    val expenseSummary: DailyExpenseSummaryResponse? = null,
    val proteinSummary: DailyProteinSummaryResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val proteinRepository: ProteinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        loadDataForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadDataForDate(date)
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val dateStr = date.toString()

            try {
                coroutineScope {
                    val expenseEntriesDef = async { expenseRepository.getDailyExpenseEntries(dateStr) }
                    val proteinEntriesDef = async { proteinRepository.getDailyProteinEntries(dateStr) }
                    val expenseSummaryDef = async { expenseRepository.getDailyExpenseSummary(dateStr) }
                    val proteinSummaryDef = async { proteinRepository.getDailyProteinSummary(dateStr) }

                    val expenseEntriesResult = expenseEntriesDef.await()
                    val proteinEntriesResult = proteinEntriesDef.await()
                    val expenseSummaryResult = expenseSummaryDef.await()
                    val proteinSummaryResult = proteinSummaryDef.await()

                    _uiState.update { 
                        it.copy(
                            expenseEntries = if (expenseEntriesResult is Result.Success) expenseEntriesResult.data else emptyList(),
                            proteinEntries = if (proteinEntriesResult is Result.Success) proteinEntriesResult.data else emptyList(),
                            expenseSummary = if (expenseSummaryResult is Result.Success) expenseSummaryResult.data else null,
                            proteinSummary = if (proteinSummaryResult is Result.Success) proteinSummaryResult.data else null,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load data"
                    )
                }
            }
        }
    }
}
