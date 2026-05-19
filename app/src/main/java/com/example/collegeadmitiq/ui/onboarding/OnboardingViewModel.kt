package com.example.collegeadmitiq.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val name: String = "",
    val grade: Int = 9,
    val gpa: String = "",
    val intendedMajor: String = "",
    val interests: MutableList<String> = mutableListOf(),
    val dreamColleges: MutableList<String> = mutableListOf(),
    val currentInterest: String = "",
    val currentCollege: String = "",
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onGradeChanged(grade: Int) {
        _uiState.value = _uiState.value.copy(grade = grade)
    }

    fun onGpaChanged(gpa: String) {
        _uiState.value = _uiState.value.copy(gpa = gpa)
    }

    fun onMajorChanged(major: String) {
        _uiState.value = _uiState.value.copy(intendedMajor = major)
    }

    fun onInterestChanged(interest: String) {
        _uiState.value = _uiState.value.copy(currentInterest = interest)
    }

    fun addInterest() {
        val interest = _uiState.value.currentInterest.trim()
        if (interest.isBlank()) return
        val updated = _uiState.value.interests.toMutableList()
        if (!updated.contains(interest)) updated.add(interest)
        _uiState.value = _uiState.value.copy(
            interests       = updated,
            currentInterest = ""
        )
    }

    fun removeInterest(interest: String) {
        val updated = _uiState.value.interests.toMutableList()
        updated.remove(interest)
        _uiState.value = _uiState.value.copy(interests = updated)
    }

    fun onCollegeChanged(college: String) {
        _uiState.value = _uiState.value.copy(currentCollege = college)
    }

    fun addCollege() {
        val college = _uiState.value.currentCollege.trim()
        if (college.isBlank()) return
        val updated = _uiState.value.dreamColleges.toMutableList()
        if (!updated.contains(college)) updated.add(college)
        _uiState.value = _uiState.value.copy(
            dreamColleges  = updated,
            currentCollege = ""
        )
    }

    fun removeCollege(college: String) {
        val updated = _uiState.value.dreamColleges.toMutableList()
        updated.remove(college)
        _uiState.value = _uiState.value.copy(dreamColleges = updated)
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < _uiState.value.totalSteps - 1) {
            _uiState.value = _uiState.value.copy(currentStep = current + 1)
        } else {
            saveProfile()
        }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1)
        }
    }

    fun isCurrentStepValid(): Boolean {
        return when (_uiState.value.currentStep) {
            0 -> _uiState.value.name.isNotBlank()
            1 -> _uiState.value.intendedMajor.isNotBlank()
            2 -> _uiState.value.interests.isNotEmpty()
            3 -> _uiState.value.dreamColleges.isNotEmpty()
            else -> true
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val profile = StudentProfile(
                    id                   = 1,
                    name                 = _uiState.value.name,
                    grade                = _uiState.value.grade,
                    gpa                  = _uiState.value.gpa.toFloatOrNull() ?: 0f,
                    intendedMajor        = _uiState.value.intendedMajor,
                    interests            = _uiState.value.interests,
                    dreamColleges        = _uiState.value.dreamColleges,
                    isOnboardingComplete = true
                )
                repository.saveProfile(profile)
                _uiState.value = _uiState.value.copy(
                    isSaving   = false,
                    isComplete = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error    = e.message
                )
            }
        }
    }
}