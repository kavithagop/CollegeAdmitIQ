import com.example.collegeadmitiq.domain.model.PortfolioCategory

data class GapAnalysis(
    val overallScore: Int,
    val categoryScores: Map<PortfolioCategory, CategoryScore>,
    val topPriorities: List<String>,
    val strengths: List<String>,
    val summary: String
)

data class CategoryScore(
    val category: PortfolioCategory,
    val score: Int,
    val status: GapStatus,
    val feedback: String,
    val itemCount: Int
)

enum class GapStatus(
    val displayName: String,
    val emoji: String
) {
    STRONG("Strong", "✅"),
    GOOD("Good", "👍"),
    WEAK("Needs Work", "⚠️"),
    MISSING("Missing", "❌")
}