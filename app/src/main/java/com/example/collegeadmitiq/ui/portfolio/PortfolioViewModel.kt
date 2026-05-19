package com.example.collegeadmitiq.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioUiState(
    val items: List<PortfolioItem>          = emptyList(),
    val selectedCategory: PortfolioCategory? = null,
    val isLoading: Boolean                  = true,
    val showAddSheet: Boolean               = false,
    val editingItem: PortfolioItem?         = null
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: CollegeAdmitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            repository.getAllPortfolioItems().collect { items ->
                _uiState.value = _uiState.value.copy(
                    items     = items,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(category: PortfolioCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun showAddSheet(item: PortfolioItem? = null) {
        _uiState.value = _uiState.value.copy(
            showAddSheet = true,
            editingItem  = item
        )
    }

    fun hideAddSheet() {
        _uiState.value = _uiState.value.copy(
            showAddSheet = false,
            editingItem  = null
        )
    }

    fun deleteItem(item: PortfolioItem) {
        viewModelScope.launch {
            repository.deletePortfolioItem(item)
        }
    }

    fun filteredItems(): List<PortfolioItem> {
        val category = _uiState.value.selectedCategory
        return if (category == null) _uiState.value.items
        else _uiState.value.items.filter { it.category == category }
    }
}