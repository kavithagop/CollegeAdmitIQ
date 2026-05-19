package com.example.collegeadmitiq.domain.model

enum class PortfolioCategory(
    val displayName: String,
    val emoji: String,
    val description: String,
    val commonAppName: String
) {
    ACADEMIC(
        "Academic",
        emoji         = "📚",
        "Academic achievements, awards, honors",
        "Academic"
    ),
    ATHLETIC(
        "Athletics",
        "⚽",
        "Sports teams, competitions, fitness activities",
        "Athletics"
    ),
    ARTS(
        "Arts & Music",
        "🎨",
        "Visual arts, music, theater, creative writing",
        "Arts"
    ),
    VOLUNTEERING(
        "Volunteering",
        "🤝",
        "Community service, nonprofits, social causes",
        "Community services"
    ),
    LEADERSHIP(
        "Leadership",
        "👑",
        "Student government, club officer, team captain",
        "School/community activity"
    ),
    RESEARCH(
        "Research",
        "🔬",
        "Lab work, independent research, publications",
        "Research"
    ),
    WORK(
        "Work Experience",
        "💼",
        "Jobs, internships, freelance work",
        "Work"
    ),
    SUMMER(
        "Summer programs",
        "☀️",
        "Summer camps, programs, courses",
        "Summer"
    ),
    AWARDS(
        "Awards & Honors",
        "🏆",
        "Scholarships, competitions, recognitions",
        "Honor/Award"
    ),
    STEM(
        "STEM",
        "💻",
        "Coding, robotics, science projects, hackathons",
        "STEM Activity"
    )
}