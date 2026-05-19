package com.example.collegeadmitiq.domain.model

data class StudentProfile(
    val id: Long,
    val name: String,
    val grade: Int = 9, // 9, 10, 11, 12
    val gpa: Float = 0f,
    val satScore: Int? = null,
    val actScore: Int? = null,
    val intendedMajor: String = "",
    val interests: List<String> = emptyList(),
    val dreamColleges: List<String> = emptyList(),
    val isOnboardingComplete: Boolean = false
)

fun Int.gradeDisplay(): String = when(this) {
    9 -> "9th grade"
    10 -> "10th grade"
    11 -> "11th grade"
    12 -> "12th grade"
    else -> "grade $this"
}