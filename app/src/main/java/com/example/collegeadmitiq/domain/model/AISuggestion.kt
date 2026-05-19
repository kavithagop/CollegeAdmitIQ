package com.example.collegeadmitiq.domain.model

data class AISuggestion (
    val id: Long = 0,
    val category: PortfolioCategory,
    val title: String,
    val description: String,
    val whyRelevant: String,
    val difficulty: SuggestionDifficulty,
    val timeCommitment: String,
    val deadline: String = "",
    val isSaved: Boolean = false,
    val isCompleted: Boolean = false
)

enum class SuggestionDifficulty(
    val displayName: String,
    val emoji: String
) {
    EASY("Easy to get", "🟢"),
    MEDIUM("Competitive", "🟡"),
    HARD("Highly Selective", "🔴")
}