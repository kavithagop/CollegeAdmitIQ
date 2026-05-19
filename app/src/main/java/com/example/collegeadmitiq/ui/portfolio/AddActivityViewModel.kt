package com.example.collegeadmitiq.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddActivityUiState(
    val category: PortfolioCategory  = PortfolioCategory.ACADEMIC,
    val title: String                = "",
    val organization: String         = "",
    val description: String          = "",
    val role: String                 = "",
    val hoursPerWeek: String         = "",
    val weeksPerYear: String         = "",
    val impact: String               = "",
    val isOngoing: Boolean           = true,
    val grade9: Boolean              = false,
    val grade10: Boolean             = false,
    val grade11: Boolean             = false,
    val grade12: Boolean             = false,
    val isSaving: Boolean            = false,
    val isSaved: Boolean             = false,
    val error: String?               = null
)

@HiltViewModel
class AddActivityViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddActivityUiState())
    val uiState: StateFlow<AddActivityUiState> = _uiState.asStateFlow()

    fun loadItem(item: PortfolioItem) {
        _uiState.value = AddActivityUiState(
            category     = item.category,
            title        = item.title,
            organization = item.organization,
            description  = item.description,
            role         = item.role,
            hoursPerWeek = item.hoursPerWeek.toString(),
            weeksPerYear = item.weeksPerYear.toString(),
            impact       = item.impact,
            isOngoing    = item.isOngoing,
            grade9       = item.grade9,
            grade10      = item.grade10,
            grade11      = item.grade11,
            grade12      = item.grade12
        )
    }

    fun onCategoryChanged(category: PortfolioCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onOrganizationChanged(org: String) {
        _uiState.value = _uiState.value.copy(organization = org)
    }

    fun onDescriptionChanged(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun onRoleChanged(role: String) {
        _uiState.value = _uiState.value.copy(role = role)
    }

    fun onHoursChanged(hours: String) {
        _uiState.value = _uiState.value.copy(hoursPerWeek = hours)
    }

    fun onWeeksChanged(weeks: String) {
        _uiState.value = _uiState.value.copy(weeksPerYear = weeks)
    }

    fun onImpactChanged(impact: String) {
        _uiState.value = _uiState.value.copy(impact = impact)
    }

    fun onOngoingChanged(ongoing: Boolean) {
        _uiState.value = _uiState.value.copy(isOngoing = ongoing)
    }

    fun onGradeToggled(grade: Int) {
        _uiState.value = when (grade) {
            9  -> _uiState.value.copy(grade9  = !_uiState.value.grade9)
            10 -> _uiState.value.copy(grade10 = !_uiState.value.grade10)
            11 -> _uiState.value.copy(grade11 = !_uiState.value.grade11)
            12 -> _uiState.value.copy(grade12 = !_uiState.value.grade12)
            else -> _uiState.value
        }
    }

    fun isValid(): Boolean =
        _uiState.value.title.isNotBlank() &&
                _uiState.value.description.isNotBlank()

    fun saveActivity(existingId: Long = 0) {
        if (!isValid()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val item = PortfolioItem(
                    id           = existingId,
                    category     = _uiState.value.category,
                    title        = _uiState.value.title,
                    organization = _uiState.value.organization,
                    description  = _uiState.value.description,
                    role         = _uiState.value.role,
                    hoursPerWeek = _uiState.value.hoursPerWeek.toIntOrNull() ?: 0,
                    weeksPerYear = _uiState.value.weeksPerYear.toIntOrNull() ?: 0,
                    impact       = _uiState.value.impact,
                    isOngoing    = _uiState.value.isOngoing,
                    grade9       = _uiState.value.grade9,
                    grade10      = _uiState.value.grade10,
                    grade11      = _uiState.value.grade11,
                    grade12      = _uiState.value.grade12
                )
                if (existingId == 0L) {
                    repository.savePortfolioItem(item)
                } else {
                    repository.updatePortfolioItem(item)
                }
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