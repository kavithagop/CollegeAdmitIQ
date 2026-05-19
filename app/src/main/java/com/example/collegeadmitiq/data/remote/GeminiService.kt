package com.example.collegeadmitiq.data.remote

import CategoryScore
import GapAnalysis
import com.example.collegeadmitiq.BuildConfig
import com.example.collegeadmitiq.domain.model.AISuggestion
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.SuggestionDifficulty
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val gson = Gson()

    private val model = GenerativeModel(
        modelName  = "gemini-3-flash-preview",
        apiKey     = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateSuggestions(
        major: String,
        interests: List<String>,
        grade: Int,
        existingCategories: List<String>
    ): List<AISuggestion> {
        val prompt = """
            You are a college admissions expert helping a ${grade}th grade student.
            Their intended major: $major
            Their interests: ${interests.joinToString(", ")}
            Activities they already have: ${existingCategories.joinToString(", ")}
            
            Suggest 6 specific activities, programs, or opportunities they should pursue.
            Respond ONLY with a valid JSON array, no markdown, no backticks:
            [
              {
                "category": "SUMMER|RESEARCH|VOLUNTEERING|LEADERSHIP|STEM|ACADEMIC|ARTS|ATHLETIC|WORK|AWARDS",
                "title": "specific activity name",
                "description": "what it is and how to get involved",
                "whyRelevant": "why this helps for their major and college apps",
                "difficulty": "EASY|MEDIUM|HARD",
                "timeCommitment": "e.g. 10 hrs/week for 8 weeks",
                "deadline": "e.g. March 15 or empty string"
              }
            ]
        """.trimIndent()

        val response = model.generateContent(prompt)
        val json = response.text ?: return emptyList()

        return try {
            val type = object : TypeToken<List<SuggestionJson>>() {}.type
            val suggestions: List<SuggestionJson> = gson.fromJson(json.trim(), type)
            suggestions.mapIndexed { index, s ->
                AISuggestion(
                    id             = index.toLong(),
                    category       = runCatching {
                        PortfolioCategory.valueOf(s.category)
                    }.getOrDefault(PortfolioCategory.ACADEMIC),
                    title          = s.title,
                    description    = s.description,
                    whyRelevant    = s.whyRelevant,
                    difficulty     = runCatching {
                        SuggestionDifficulty.valueOf(s.difficulty)
                    }.getOrDefault(SuggestionDifficulty.MEDIUM),
                    timeCommitment = s.timeCommitment,
                    deadline       = s.deadline
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun analyzeGaps(
        major: String,
        grade: Int,
        dreamColleges: List<String>,
        portfolioSummary: String
    ): GapAnalysis {
        val prompt = """
            You are a college admissions expert.
            Student info:
            - Grade: ${grade}th
            - Major: $major
            - Dream colleges: ${dreamColleges.joinToString(", ")}
            - Current portfolio: $portfolioSummary
            
            Analyze their portfolio gaps and respond ONLY with valid JSON,
            no markdown, no backticks:
            {
              "overallScore": <0-100>,
              "summary": "2-3 sentence overall assessment",
              "strengths": ["strength 1", "strength 2"],
              "topPriorities": ["priority action 1", "priority action 2", "priority action 3"],
              "categoryScores": [
                {
                  "category": "ACADEMIC|ATHLETIC|ARTS|VOLUNTEERING|LEADERSHIP|RESEARCH|WORK|SUMMER|AWARDS|STEM",
                  "score": <0-100>,
                  "status": "STRONG|GOOD|WEAK|MISSING",
                  "feedback": "specific feedback",
                  "itemCount": <number>
                }
              ]
            }
        """.trimIndent()

        val response = model.generateContent(prompt)
        val json = response.text ?: return defaultGapAnalysis()

        return try {
            val raw = gson.fromJson(json.trim(), GapAnalysisJson::class.java)
            GapAnalysis(
                overallScore   = raw.overallScore,
                summary        = raw.summary,
                strengths      = raw.strengths,
                topPriorities  = raw.topPriorities,
                categoryScores = raw.categoryScores.associate { cs ->
                    val cat = runCatching {
                        PortfolioCategory.valueOf(cs.category)
                    }.getOrDefault(PortfolioCategory.ACADEMIC)
                    cat to CategoryScore(
                        category = cat,
                        score = cs.score,
                        status = runCatching {
                            GapStatus.valueOf(cs.status)
                        }.getOrDefault(GapStatus.WEAK),
                        feedback = cs.feedback,
                        itemCount = cs.itemCount
                    )
                }
            )
        } catch (e: Exception) {
            defaultGapAnalysis()
        }
    }

    private fun defaultGapAnalysis() = GapAnalysis(
        overallScore   = 0,
        summary        = "Unable to analyze portfolio at this time.",
        strengths      = emptyList(),
        topPriorities  = emptyList(),
        categoryScores = emptyMap()
    )

    // ── JSON helper classes ───────────────────────────────────────────────────
    private data class SuggestionJson(
        val category: String,
        val title: String,
        val description: String,
        val whyRelevant: String,
        val difficulty: String,
        val timeCommitment: String,
        val deadline: String
    )

    private data class GapAnalysisJson(
        val overallScore: Int,
        val summary: String,
        val strengths: List<String>,
        val topPriorities: List<String>,
        val categoryScores: List<CategoryScoreJson>
    )

    private data class CategoryScoreJson(
        val category: String,
        val score: Int,
        val status: String,
        val feedback: String,
        val itemCount: Int
    )
}