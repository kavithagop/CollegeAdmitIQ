package com.example.collegeadmitiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Long = 1,
    val name: String,
    val grade: Int,
    val gpa: Float,
    val satScore: Int?,
    val actScore: Int?,
    val intendedMajor: String,
    val interestsJson: String,      // JSON array of strings
    val dreamCollegesJson: String,  // JSON array of strings
    val isOnboardingComplete: Boolean
)