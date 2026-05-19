package com.example.collegeadmitiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("portfolio_items")
data class PortfolioItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val title: String,
    val organization: String,
    val description: String,
    val role: String,
    val startDate: Long,
    val endDate: Long?,
    val isOngoing: Boolean,
    val hoursPerWeek: Int,
    val weeksPerYear: Int,
    val impact: String,
    val grade9: Boolean,
    val grade10: Boolean,
    val grade11: Boolean,
    val grade12: Boolean
)