package com.example.collegeadmitiq.data.repository

import GapAnalysis
import com.example.collegeadmitiq.data.local.*
import com.example.collegeadmitiq.data.remote.GeminiService
import com.example.collegeadmitiq.domain.model.AISuggestion
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem
import com.example.collegeadmitiq.domain.model.StudentProfile
import com.example.collegeadmitiq.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollegeAdmitRepository @Inject constructor(
    private val studentProfileDao: StudentProfileDao,
    private val portfolioItemDao: PortfolioItemDao,
    private val aiSuggestionDao: AISuggestionDao,
    private val geminiService: GeminiService
) {
    private val gson = Gson()

    // ── Student Profile ───────────────────────────────────────────────────────
    fun getProfile(): Flow<StudentProfile?> =
        studentProfileDao.getProfile().map { it?.toDomain() }

    suspend fun getProfileOnce(): StudentProfile? =
        studentProfileDao.getProfileOnce()?.toDomain()

    suspend fun saveProfile(profile: StudentProfile) {
        studentProfileDao.saveProfile(profile.toEntity())
    }

    // ── Portfolio Items ───────────────────────────────────────────────────────
    fun getAllPortfolioItems(): Flow<List<PortfolioItem>> =
        portfolioItemDao.getAllItems().map { items ->
            items.map { it.toDomain() }
        }

    fun getItemsByCategory(category: PortfolioCategory): Flow<List<PortfolioItem>> =
        portfolioItemDao.getItemsByCategory(category.name).map { items ->
            items.map { it.toDomain() }
        }

    suspend fun savePortfolioItem(item: PortfolioItem): Long =
        portfolioItemDao.insertItem(item.toEntity())

    suspend fun updatePortfolioItem(item: PortfolioItem) =
        portfolioItemDao.updateItem(item.toEntity())

    suspend fun deletePortfolioItem(item: PortfolioItem) =
        portfolioItemDao.deleteItem(item.toEntity())

    fun getTotalItemCount(): Flow<Int> =
        portfolioItemDao.getTotalCount()

    // ── AI Suggestions ────────────────────────────────────────────────────────
    fun getAllSuggestions(): Flow<List<AISuggestion>> =
        aiSuggestionDao.getAllSuggestions().map { it.map { s -> s.toDomain() } }

    fun getSavedSuggestions(): Flow<List<AISuggestion>> =
        aiSuggestionDao.getSavedSuggestions().map { it.map { s -> s.toDomain() } }

    suspend fun generateAndSaveSuggestions(profile: StudentProfile, existingCategories: List<String>) {
        aiSuggestionDao.clearUnsavedSuggestions()
        val suggestions = geminiService.generateSuggestions(
            major              = profile.intendedMajor,
            interests          = profile.interests,
            grade              = profile.grade,
            existingCategories = existingCategories
        )
        aiSuggestionDao.insertSuggestions(suggestions.map { it.toEntity() })
    }

    suspend fun toggleSuggestionSaved(id: Long, isSaved: Boolean) =
        aiSuggestionDao.updateSaved(id, isSaved)

    suspend fun toggleSuggestionCompleted(id: Long, isCompleted: Boolean) =
        aiSuggestionDao.updateCompleted(id, isCompleted)

    // ── Gap Analysis ──────────────────────────────────────────────────────────
    suspend fun analyzeGaps(
        profile: StudentProfile,
        portfolioItems: List<PortfolioItem>
    ): GapAnalysis {
        val summary = portfolioItems
            .groupBy { it.category }
            .entries
            .joinToString(", ") { (cat, items) ->
                "${cat.displayName}: ${items.size} items"
            }
        return geminiService.analyzeGaps(
            major            = profile.intendedMajor,
            grade            = profile.grade,
            dreamColleges    = profile.dreamColleges,
            portfolioSummary = summary.ifEmpty { "No activities yet" }
        )
    }

    // ── Mappers ───────────────────────────────────────────────────────────────
    private fun StudentProfileEntity.toDomain(): StudentProfile {
        val listType = object : TypeToken<List<String>>() {}.type
        return StudentProfile(
            id                   = id,
            name                 = name,
            grade                = grade,
            gpa                  = gpa,
            satScore             = satScore,
            actScore             = actScore,
            intendedMajor        = intendedMajor,
            interests            = gson.fromJson(interestsJson, listType),
            dreamColleges        = gson.fromJson(dreamCollegesJson, listType),
            isOnboardingComplete = isOnboardingComplete
        )
    }

    private fun StudentProfile.toEntity() = StudentProfileEntity(
        id                   = id,
        name                 = name,
        grade                = grade,
        gpa                  = gpa,
        satScore             = satScore,
        actScore             = actScore,
        intendedMajor        = intendedMajor,
        interestsJson        = gson.toJson(interests),
        dreamCollegesJson    = gson.toJson(dreamColleges),
        isOnboardingComplete = isOnboardingComplete
    )

    private fun PortfolioItemEntity.toDomain() = PortfolioItem(
        id           = id,
        category     = PortfolioCategory.valueOf(category),
        title        = title,
        organization = organization,
        description  = description,
        role         = role,
        startDate    = startDate,
        endDate      = endDate,
        isOngoing    = isOngoing,
        hoursPerWeek = hoursPerWeek,
        weeksPerYear = weeksPerYear,
        impact       = impact,
        grade9       = grade9,
        grade10      = grade10,
        grade11      = grade11,
        grade12      = grade12
    )

    private fun PortfolioItem.toEntity() = PortfolioItemEntity(
        id           = id,
        category     = category.name,
        title        = title,
        organization = organization,
        description  = description,
        role         = role,
        startDate    = startDate,
        endDate      = endDate,
        isOngoing    = isOngoing,
        hoursPerWeek = hoursPerWeek,
        weeksPerYear = weeksPerYear,
        impact       = impact,
        grade9       = grade9,
        grade10      = grade10,
        grade11      = grade11,
        grade12      = grade12
    )

    private fun AISuggestionEntity.toDomain() = AISuggestion(
        id             = id,
        category       = PortfolioCategory.valueOf(category),
        title          = title,
        description    = description,
        whyRelevant    = whyRelevant,
        difficulty     = SuggestionDifficulty.valueOf(difficulty),
        timeCommitment = timeCommitment,
        deadline       = deadline,
        isSaved        = isSaved,
        isCompleted    = isCompleted
    )

    private fun AISuggestion.toEntity() = AISuggestionEntity(
        id             = id,
        category       = category.name,
        title          = title,
        description    = description,
        whyRelevant    = whyRelevant,
        difficulty     = difficulty.name,
        timeCommitment = timeCommitment,
        deadline       = deadline,
        isSaved        = isSaved,
        isCompleted    = isCompleted
    )
}