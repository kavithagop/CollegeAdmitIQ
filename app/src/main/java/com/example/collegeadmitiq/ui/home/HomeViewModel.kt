package com.example.collegeadmitiq.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem
import com.example.collegeadmitiq.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val profile: StudentProfile? = null,
    val totalItems: Int = 0,
    val recentItems: List<PortfolioItem> = emptyList(),
    val categoryProgress: Map<PortfolioCategory, Int> = emptyMap(),
    val portfolioScore: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getProfile(),
        repository.getAllPortfolioItems()
    ) { profile, items ->
        val categoryProgress = PortfolioCategory.entries.associateWith { cat ->
            items.count { it.category == cat }
        }
        val score = calculateScore(items)
        HomeUiState(
            profile          = profile,
            totalItems       = items.size,
            recentItems      = items.take(3),
            categoryProgress = categoryProgress,
            portfolioScore   = score,
            isLoading        = false
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    private fun calculateScore(items: List<PortfolioItem>): Int {
        if (items.isEmpty()) return 0
        val categoriesCovered = items.map { it.category }.distinct().size
        val totalCategories   = PortfolioCategory.entries.size
        val categoryScore     = (categoriesCovered.toFloat() / totalCategories * 60).toInt()
        val volumeScore       = minOf(items.size * 4, 40)
        return minOf(categoryScore + volumeScore, 100)
    }
}