package com.example.collegeadmitiq.domain.model

data class PortfolioItem(
    val id: Long = 0,
    val category: PortfolioCategory,
    val title: String,
    val organization: String ="",
    val description: String,
    val role: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isOngoing: Boolean = true,
    val hoursPerWeek: Int = 0,
    val weeksPerYear: Int = 0,
    val impact: String = "",
    val grade9: Boolean = false,
    val grade10: Boolean = false,
    val grade11: Boolean = false,
    val grade12: Boolean = false
) {
    // Total hours calculated automatically
    val totalHours: Int get() = hoursPerWeek * weeksPerYear
}