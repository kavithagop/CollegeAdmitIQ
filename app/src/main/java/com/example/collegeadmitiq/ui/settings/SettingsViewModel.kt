package com.example.collegeadmitiq.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val name: String                    = "",
    val grade: Int                      = 9,
    val gpa: String                     = "",
    val intendedMajor: String           = "",
    val satScore: String                = "",
    val actScore: String                = "",
    val interests: MutableList<String>  = mutableListOf(),
    val dreamColleges: MutableList<String> = mutableListOf(),
    val currentInterest: String         = "",
    val currentCollege: String          = "",
    val isSaving: Boolean               = false,
    val isSaved: Boolean                = false,
    val error: String?                  = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    // With this — only loads ONCE, doesn't keep overwriting:
    private fun loadProfile() {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            profile?.let {
                _uiState.value = SettingsUiState(
                    name          = it.name,
                    grade         = it.grade,
                    gpa           = if (it.gpa > 0f) it.gpa.toString() else "",
                    intendedMajor = it.intendedMajor,
                    satScore      = it.satScore?.toString() ?: "",
                    actScore      = it.actScore?.toString() ?: "",
                    interests     = it.interests.toMutableList(),
                    dreamColleges = it.dreamColleges.toMutableList()
                )
            }
        }
    }

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

    fun onSatChanged(sat: String) {
        _uiState.value = _uiState.value.copy(satScore = sat)
    }

    fun onActChanged(act: String) {
        _uiState.value = _uiState.value.copy(actScore = act)
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

    fun isValid(): Boolean =
        _uiState.value.name.isNotBlank() &&
                _uiState.value.intendedMajor.isNotBlank()

    fun saveProfile() {
        if (!isValid()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val profile = StudentProfile(
                    id                   = 1,
                    name                 = _uiState.value.name,
                    grade                = _uiState.value.grade,
                    gpa                  = _uiState.value.gpa.toFloatOrNull() ?: 0f,
                    intendedMajor        = _uiState.value.intendedMajor,
                    satScore             = _uiState.value.satScore.toIntOrNull(),
                    actScore             = _uiState.value.actScore.toIntOrNull(),
                    interests            = _uiState.value.interests,
                    dreamColleges        = _uiState.value.dreamColleges,
                    isOnboardingComplete = true
                )
                repository.saveProfile(profile)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved  = true
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