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

    val listUiState: StateFlow<ProteinListUiState> =
        _listUiState.asStateFlow()

    init {
        viewModelScope.launch {
            proteinRepository
                .getProteins()
                .collect { proteins ->
                    _listUiState.update {
                        it.copy(proteins = proteins)
                    }
                }
        }
    }


    private val _editUiState = MutableStateFlow(ProteinEditUiState())

    val editUiState: StateFlow<ProteinEditUiState> =
        _editUiState.asStateFlow()


    fun syncProteins() {
        viewModelScope.launch {

            _listUiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            val result = proteinRepository.syncProteins()

            when (result) {

                is Result.Loading -> {
                    // Loading is already represented by the UI state.
                }

                is Result.Success -> {
                    _listUiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }

                is Result.Error -> {
                    _listUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                        )
                    }
                }
            }
        }
    }

    fun onListErrorShown() {
        _listUiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun loadProtein(protein: ProteinEntity) {
        _editUiState.update {
            ProteinEditUiState(
                foodName = protein.foodName,
                proteinGrams = protein.proteinGrams.toString(),
                calories = protein.calories?.toString() ?: "",
                note = protein.note ?: "",
                date = protein.date,
            )
        }
    }

    fun onFoodNameChange(value: String) {
        _editUiState.update {
            it.copy(foodName = value)
        }
    }

    fun onProteinGramsChange(value: String) {
        _editUiState.update {
            it.copy(proteinGrams = value)
        }
    }

    fun onCaloriesChange(value: String) {
        _editUiState.update {
            it.copy(calories = value)
        }
    }

    fun onNoteChange(value: String) {
        _editUiState.update {
            it.copy(note = value)
        }
    }

    fun onDateChange(value: String) {
        _editUiState.update {
            it.copy(date = value)
        }
    }

    fun saveProtein(existingId: Long?) {

        val state = _editUiState.value

        val grams = state.proteinGrams.toDoubleOrNull()

        if (grams == null) {
            _editUiState.update {
                it.copy(
                    errorMessage = "Enter a valid protein amount"
                )
            }
            return
        }

        val cal = state.calories
            .takeIf { it.isNotBlank() }
            ?.toIntOrNull()

        viewModelScope.launch {

            _editUiState.update {
                it.copy(
                    isLoading = true,
                    isSaved = false,
                    errorMessage = null,
                )
            }

            val result = if (existingId == null) {

                proteinUseCase.createProtein(
                    foodName = state.foodName,
                    proteinGrams = grams,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = state.date,
                )

            } else {

                proteinUseCase.updateProtein(
                    id = existingId.toString(),
                    foodName = state.foodName,
                    proteinGrams = grams,
                    calories = cal,
                    note = state.note.ifBlank { null },
                    date = state.date,
                )
            }

            when (result) {

                is Result.Loading -> {
                    // Loading is already represented by isLoading = true.
                }

                is Result.Success -> {
                    _editUiState.update {
                        it.copy(
                            isLoading = false,
                            isSaved = true,
                        )
                    }
                }

                is Result.Error -> {
                    _editUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                        )
                    }
                }
            }
        }
    }


    fun deleteProtein(id: Long) {

        viewModelScope.launch {

            val result = proteinUseCase.deleteProtein(id.toString())

            when (result) {

                is Result.Loading -> {
                    // Nothing to do.
                }

                is Result.Success -> {
                    // Room Flow automatically updates the list.
                }

                is Result.Error -> {
                    _listUiState.update {
                        it.copy(
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun onEditErrorShown() {
        _editUiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun resetEditState() {
        _editUiState.update {
            ProteinEditUiState()
        }
    }
}