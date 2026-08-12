package com.jaydeep.trackingapp.features.protein.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.features.protein.domain.ProteinUseCase
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

data class ProteinListUiState(
    val proteins: List<ProteinEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class ProteinEditUiState(
    val foodName: String = "",
    val gramsConsumed: String = "",
    val proteinGrams: String = "",
    val calories: String = "",
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val foodNameError: String? = null,
    val gramsError: String? = null,
    val proteinGramsError: String? = null,
)

@HiltViewModel
class ProteinViewModel @Inject constructor(
    private val proteinUseCase: ProteinUseCase,
    private val proteinRepository: ProteinRepository,
) : ViewModel() {

    private val _listUiState = MutableStateFlow(ProteinListUiState())
    val listUiState: StateFlow<ProteinListUiState> = _listUiState.asStateFlow()

    init {
        viewModelScope.launch {
            proteinRepository.getProteins().collect { proteins ->
                _listUiState.update { it.copy(proteins = proteins) }
            }
        }
    }

    private val _editUiState = MutableStateFlow(ProteinEditUiState())
    val editUiState: StateFlow<ProteinEditUiState> = _editUiState.asStateFlow()

    fun syncProteins() {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = proteinRepository.syncProteins()) {
                is Result.Loading -> Unit
                is Result.Success -> _listUiState.update { it.copy(isLoading = false) }
                is Result.Error -> _listUiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun onListErrorShown() = _listUiState.update { it.copy(errorMessage = null) }

    fun loadProteinById(id: Long) {
        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true) }
            proteinRepository.getProteinById(id)?.let { protein ->
                val date = try {
                    LocalDate.parse(protein.date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
                _editUiState.update {
                    ProteinEditUiState(
                        foodName = protein.foodName,
                        gramsConsumed = protein.gramsConsumed.toString(),
                        proteinGrams = protein.proteinGrams.toString(),
                        calories = protein.calories?.toString() ?: "",
                        note = protein.note ?: "",
                        dateMillis = date,
                        isLoading = false
                    )
                }
            } ?: _editUiState.update { it.copy(isLoading = false, errorMessage = "Protein entry not found") }
        }
    }

    fun onFoodNameChange(value: String) = _editUiState.update { it.copy(foodName = value, foodNameError = null) }
    fun onGramsConsumedChange(value: String) = _editUiState.update { it.copy(gramsConsumed = value, gramsError = null) }
    fun onProteinGramsChange(value: String) = _editUiState.update { it.copy(proteinGrams = value, proteinGramsError = null) }
    fun onCaloriesChange(value: String) = _editUiState.update { it.copy(calories = value) }
    fun onNoteChange(value: String) = _editUiState.update { it.copy(note = value) }
    fun onDateChange(value: Long) = _editUiState.update { it.copy(dateMillis = value) }

    fun onKeyPress(key: String) {
        val current = _editUiState.value.proteinGrams
        val newValue = when (key) {
            "." -> if (!current.contains(".")) current + "." else current
            else -> current + key
        }
        onProteinGramsChange(newValue)
    }

    fun onBackspace() {
        val current = _editUiState.value.proteinGrams
        if (current.isNotEmpty()) {
            onProteinGramsChange(current.dropLast(1))
        }
    }

    private fun validate(): Boolean {
        val state = _editUiState.value
        val nameErr = if (state.foodName.isBlank()) "Source is required" else null
        val gramErr = when {
            state.gramsConsumed.isBlank() -> "Grams consumed is required"
            state.gramsConsumed.toDoubleOrNull() == null -> "Invalid number"
            state.gramsConsumed.toDouble() <= 0 -> "Must be greater than 0"
            else -> null
        }
        val proteinErr = when {
            state.proteinGrams.isBlank() -> "Protein amount is required"
            state.proteinGrams.toDoubleOrNull() == null -> "Invalid number"
            state.proteinGrams.toDouble() <= 0 -> "Must be greater than 0"
            else -> null
        }
        _editUiState.update { 
            it.copy(
                foodNameError = nameErr, 
                gramsError = gramErr,
                proteinGramsError = proteinErr
            ) 
        }
        return nameErr == null && gramErr == null && proteinErr == null
    }

    fun saveProtein(existingId: Long?) {
        if (!validate()) return

        val state = _editUiState.value
        val proteinGramsValue = state.proteinGrams.toDoubleOrNull() ?: 0.0
        val gramsConsumedValue = state.gramsConsumed.toDouble()
        val cal = state.calories.takeIf { it.isNotBlank() }?.toIntOrNull()
        val dateString = Instant.ofEpochMilli(state.dateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true, isSaved = false, errorMessage = null) }

            val result = if (existingId == null) {
                proteinUseCase.createProtein(
                    foodName = state.foodName,
                    gramsConsumed = gramsConsumedValue,
                    proteinGrams = proteinGramsValue,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = dateString,
                )
            } else {
                proteinUseCase.updateProtein(
                    id = existingId,
                    foodName = state.foodName,
                    gramsConsumed = gramsConsumedValue,
                    proteinGrams = proteinGramsValue,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = dateString,
                )
            }

            when (result) {
                is Result.Loading -> Unit
                is Result.Success -> _editUiState.update { it.copy(isLoading = false, isSaved = true) }
                is Result.Error -> _editUiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun deleteProtein(id: Long) {
        viewModelScope.launch {
            when (val result = proteinUseCase.deleteProtein(id)) {
                is Result.Loading -> Unit
                is Result.Success -> Unit
                is Result.Error -> _listUiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun onEditErrorShown() = _editUiState.update { it.copy(errorMessage = null) }
    fun resetEditState() = _editUiState.update { ProteinEditUiState() }
}
