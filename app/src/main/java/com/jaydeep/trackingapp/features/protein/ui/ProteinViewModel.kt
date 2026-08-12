package com.jaydeep.trackingapp.features.protein.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import com.jaydeep.trackingapp.core.data.repository.ProteinRepository
import com.jaydeep.trackingapp.features.auth.domain.ProteinUseCase
import com.jaydeep.trackingapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    val date: String = LocalDate.now().toString(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
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

    fun loadProtein(protein: ProteinEntity) {
        _editUiState.update {
            ProteinEditUiState(
                foodName = protein.foodName,
                gramsConsumed = protein.gramsConsumed.toString(),
                proteinGrams = protein.proteinGrams.toString(),
                calories = protein.calories?.toString() ?: "",
                note = protein.note ?: "",
                date = protein.date,
            )
        }
    }

    fun loadProteinById(id: String) {
        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true) }
            proteinRepository.getProteinById(id)?.let { protein ->
                _editUiState.update {
                    ProteinEditUiState(
                        foodName = protein.foodName,
                        gramsConsumed = protein.gramsConsumed.toString(),
                        proteinGrams = protein.proteinGrams.toString(),
                        calories = protein.calories?.toString() ?: "",
                        note = protein.note ?: "",
                        date = protein.date,
                        isLoading = false
                    )
                }
            } ?: _editUiState.update { it.copy(isLoading = false, errorMessage = "Protein entry not found") }
        }
    }

    fun onFoodNameChange(value: String) = _editUiState.update { it.copy(foodName = value) }
    fun onGramsConsumedChange(value: String) = _editUiState.update { it.copy(gramsConsumed = value) }
    fun onProteinGramsChange(value: String) = _editUiState.update { it.copy(proteinGrams = value) }
    fun onCaloriesChange(value: String) = _editUiState.update { it.copy(calories = value) }
    fun onNoteChange(value: String) = _editUiState.update { it.copy(note = value) }
    fun onDateChange(value: String) = _editUiState.update { it.copy(date = value) }

    fun saveProtein(existingId: Long?) {
        val state = _editUiState.value
        val proteinGramsValue = state.proteinGrams.toDoubleOrNull()
        val gramsConsumedValue = state.gramsConsumed.toDoubleOrNull()
        
        if (proteinGramsValue == null || gramsConsumedValue == null) {
            _editUiState.update { it.copy(errorMessage = "Enter valid numeric values") }
            return
        }
        val cal = state.calories.takeIf { it.isNotBlank() }?.toIntOrNull()

        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true, isSaved = false, errorMessage = null) }

            val result = if (existingId == null) {
                proteinUseCase.createProtein(
                    foodName = state.foodName,
                    gramsConsumed = gramsConsumedValue,
                    proteinGrams = proteinGramsValue,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = state.date,
                )
            } else {
                proteinUseCase.updateProtein(
                    id = existingId.toString(),
                    foodName = state.foodName,
                    gramsConsumed = gramsConsumedValue,
                    proteinGrams = proteinGramsValue,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = state.date,
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
            when (val result = proteinUseCase.deleteProtein(id.toString())) {
                is Result.Loading -> Unit
                is Result.Success -> Unit
                is Result.Error -> _listUiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun onEditErrorShown() = _editUiState.update { it.copy(errorMessage = null) }
    fun resetEditState() = _editUiState.update { ProteinEditUiState() }
}