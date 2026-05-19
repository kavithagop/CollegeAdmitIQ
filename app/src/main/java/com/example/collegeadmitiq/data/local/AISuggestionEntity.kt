package com.example.collegeadmitiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("ai_suggestions")
data class AISuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val title: String,
    val description: String,
    val whyRelevant: String,
    val difficulty: String,
    val timeCommitment: String,
    val deadline: String,
    val isSaved: Boolean,
    val isCompleted: Boolean
)