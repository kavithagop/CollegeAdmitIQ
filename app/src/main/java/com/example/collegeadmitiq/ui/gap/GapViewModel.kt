package com.example.collegeadmitiq.ui.gap

import GapAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GapUiState(
    val isLoading: Boolean    = false,
    val isAnalyzing: Boolean  = false,
    val gapAnalysis: GapAnalysis? = null,
    val error: String?        = null
)

@HiltViewModel
class GapViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GapUiState())
    val uiState: StateFlow<GapUiState> = _uiState.asStateFlow()

    fun analyzeGaps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnalyzing = true,
                error       = null
            )
            try {
                val profile = repository.getProfileOnce()
                val items   = repository.getAllPortfolioItems().first()

                if (profile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        error       = "Please complete your profile first"
                    )
                    return@launch
                }

                val analysis = repository.analyzeGaps(profile, items)
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    gapAnalysis = analysis
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    error       = "Analysis failed: ${e.message}"
                )
            }
        }
    }
}