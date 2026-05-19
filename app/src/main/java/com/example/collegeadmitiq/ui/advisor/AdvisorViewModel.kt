package com.example.collegeadmitiq.ui.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.AISuggestion
import com.example.collegeadmitiq.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdvisorUiState(
    val profile: StudentProfile?       = null,
    val suggestions: List<AISuggestion> = emptyList(),
    val isLoading: Boolean             = false,
    val isGenerating: Boolean          = false,
    val error: String?                 = null
)

@HiltViewModel
class AdvisorViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdvisorUiState())
    val uiState: StateFlow<AdvisorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getProfile(),
                repository.getAllSuggestions()
            ) { profile, suggestions ->
                AdvisorUiState(
                    profile     = profile,
                    suggestions = suggestions,
                    isLoading   = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun generateSuggestions() {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                error        = null
            )
            try {
                // Get existing categories
                val existingCategories = repository
                    .getAllPortfolioItems()
                    .first()
                    .map { it.category.displayName }
                    .distinct()

                repository.generateAndSaveSuggestions(
                    profile            = profile,
                    existingCategories = existingCategories
                )
                _uiState.value = _uiState.value.copy(isGenerating = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error        = "Failed to generate suggestions: ${e.message}"
                )
            }
        }
    }

    fun toggleSaved(suggestion: AISuggestion) {
        viewModelScope.launch {
            repository.toggleSuggestionSaved(
                id      = suggestion.id,
                isSaved = !suggestion.isSaved
            )
        }
    }

    fun toggleCompleted(suggestion: AISuggestion) {
        viewModelScope.launch {
            repository.toggleSuggestionCompleted(
                id          = suggestion.id,
                isCompleted = !suggestion.isCompleted
            )
        }
    }
}