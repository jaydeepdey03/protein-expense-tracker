package com.jaydeep.trackingapp.features.expenses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.local.entities.ExpenseEntity
import com.jaydeep.trackingapp.core.data.repository.ExpenseRepository
import com.jaydeep.trackingapp.features.expenses.domain.ExpenseUseCase
import com.jaydeep.trackingapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ExpenseListUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class ExpenseEditUiState(
    val description: String = "",
    val category: String = "Food", // Default category
    val categories: List<String> = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment"),
    val amount: String = "",
    val currency: String = "INR",
    val dateMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val descriptionError: String? = null,
    val amountError: String? = null,
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseUseCase: ExpenseUseCase,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    private val _listUiState = MutableStateFlow(ExpenseListUiState())
    val listUiState: StateFlow<ExpenseListUiState> = _listUiState.asStateFlow()

    init {
        viewModelScope.launch {
            expenseRepository.getExpenses().collect { expenses ->
                _listUiState.update { it.copy(expenses = expenses) }
            }
        }
    }

    private val _editUiState = MutableStateFlow(ExpenseEditUiState())
    val editUiState: StateFlow<ExpenseEditUiState> = _editUiState.asStateFlow()

    fun syncExpenses() {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = expenseRepository.syncExpenses()) {
                is Result.Loading -> Unit
                is Result.Success -> _listUiState.update { it.copy(isLoading = false) }
                is Result.Error -> _listUiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun onListErrorShown() = _listUiState.update { it.copy(errorMessage = null) }

    fun loadExpenseById(id: String) {
        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true) }
            expenseRepository.getExpenseById(id)?.let { expense ->
                val date = try {
                    LocalDate.parse(expense.date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
                _editUiState.update {
                    ExpenseEditUiState(
                        description = expense.title,
                        category = expense.category,
                        amount = expense.amount.toString(),
                        currency = expense.currency,
                        dateMillis = date,
                        isLoading = false
                    )
                }
            } ?: _editUiState.update { it.copy(isLoading = false, errorMessage = "Expense not found") }
        }
    }

    fun onDescriptionChange(value: String) = _editUiState.update { it.copy(description = value, descriptionError = null) }
    fun onCategoryChange(value: String) = _editUiState.update { it.copy(category = value) }
    fun onAmountChange(value: String) = _editUiState.update { it.copy(amount = value, amountError = null) }
    fun onCurrencyChange(value: String) = _editUiState.update { it.copy(currency = value) }
    fun onDateChange(value: Long) = _editUiState.update { it.copy(dateMillis = value) }

    fun onKeyPress(key: String) {
        val current = _editUiState.value.amount
        val newValue = when (key) {
            "." -> if (!current.contains(".")) current + "." else current
            else -> current + key
        }
        onAmountChange(newValue)
    }

    fun onBackspace() {
        val current = _editUiState.value.amount
        if (current.isNotEmpty()) {
            onAmountChange(current.dropLast(1))
        }
    }

    private fun validate(): Boolean {
        val state = _editUiState.value
        val descError = if (state.description.isBlank()) "Description is required" else null
        val amtError = when {
            state.amount.isBlank() -> "Amount is required"
            state.amount.toDoubleOrNull() == null -> "Invalid number"
            state.amount.toDouble() <= 0 -> "Must be greater than 0"
            else -> null
        }
        _editUiState.update { it.copy(descriptionError = descError, amountError = amtError) }
        return descError == null && amtError == null
    }

    fun saveExpense(existingId: String?) {
        if (!validate()) return

        val state = _editUiState.value
        val amount = state.amount.toDouble()
        val dateString = Instant.ofEpochMilli(state.dateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true, isSaved = false, errorMessage = null) }

            val result = if (existingId == null) {
                expenseUseCase.createExpense(
                    description = state.description,
                    category = state.category,
                    amount = amount,
                    currency = state.currency,
                    entryDate = dateString,
                )
            } else {
                expenseUseCase.updateExpense(
                    id = existingId,
                    description = state.description,
                    category = state.category,
                    amount = amount,
                    currency = state.currency,
                    entryDate = dateString,
                )
            }

            when (result) {
                is Result.Loading -> Unit
                is Result.Success -> _editUiState.update { it.copy(isLoading = false, isSaved = true) }
                is Result.Error -> _editUiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            when (val result = expenseUseCase.deleteExpense(id)) {
                is Result.Loading -> Unit
                is Result.Success -> Unit
                is Result.Error -> _listUiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun onEditErrorShown() = _editUiState.update { it.copy(errorMessage = null) }
    fun resetEditState() = _editUiState.update { ExpenseEditUiState() }
}
