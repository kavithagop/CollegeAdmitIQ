package com.example.collegeadmitiq.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem
import com.example.collegeadmitiq.domain.model.StudentProfile
import com.example.collegeadmitiq.domain.model.gradeDisplay

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F1FF)  // Slightly lighter navy
private val AccentBlue     = Color(0xFFF0F6FF)
private val AccentPurple   = Color(0xFF7C3AED)
private val AccentGreen    = Color(0xFF059669)
private val AccentAmber    = Color(0xFFD97706)
private val AccentRed      = Color(0xFFDC2626)
private val TextPrimary    = Color(0xFF0F172A)
private val TextSecondary  = Color(0xFF64748B)
private val BorderSubtle   = Color(0xFF1E3A5F)

@Composable
fun HomeScreen(
    onAddActivity: () -> Unit,
    onViewPortfolio: () -> Unit,
    onViewAdvisor: () -> Unit,
    onViewGapAnalysis: () -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Header ─────────────────────────────────────────────────────────
        HeaderSection(
            profile      = uiState.profile,
            onAddActivity = onAddActivity,
            onSettings    = onSettings
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Portfolio Score Card ────────────────────────────────────────────
        PortfolioScoreCard(
            score       = uiState.portfolioScore,
            totalItems  = uiState.totalItems,
            onViewGap   = onViewGapAnalysis
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Quick Actions ───────────────────────────────────────────────────
        Text(
            text          = "QUICK ACTIONS",
            color         = TextSecondary,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        QuickActionsRow(
            onAddActivity   = onAddActivity,
            onViewPortfolio = onViewPortfolio,
            onViewAdvisor   = onViewAdvisor,
            onViewGap       = onViewGapAnalysis
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Category Progress ───────────────────────────────────────────────
        Text(
            text          = "PORTFOLIO COVERAGE",
            color         = TextSecondary,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        CategoryProgressGrid(categoryProgress = uiState.categoryProgress)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Recent Activities ───────────────────────────────────────────────
        if (uiState.recentItems.isNotEmpty()) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text          = "RECENT ACTIVITIES",
                    color         = TextSecondary,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                TextButton(onClick = onViewPortfolio) {
                    Text(
                        text     = "See All →",
                        color    = AccentBlue,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            uiState.recentItems.forEach { item ->
                RecentActivityCard(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun HeaderSection(
    profile: StudentProfile?,
    onAddActivity: () -> Unit,
    onSettings: () -> Unit        // ← add this parameter
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Top
    ) {
        Column {
            Text(
                text     = "Hi, ${profile?.name ?: "Student"} 👋",
                color    = TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text       = "CollegeAdmitIQ",
                color      = TextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black
            )
            if (profile != null) {
                // Show SAT/ACT if available
                val scoreText = buildString {
                    if (profile.satScore != null) append("SAT ${profile.satScore}")
                    if (profile.satScore != null && profile.actScore != null) append(" • ")
                    if (profile.actScore != null) append("ACT ${profile.actScore}")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text     = "${profile.grade}th Grade • ${profile.intendedMajor}",
                        color    = AccentBlue,
                        fontSize = 12.sp
                    )
                }
                if (scoreText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text     = scoreText,
                            color    = AccentPurple,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Action buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Settings button
            IconButton(
                onClick  = onSettings,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint               = TextSecondary,
                    modifier           = Modifier.size(20.dp)
                )
            }
            // Add button
            FloatingActionButton(
                onClick        = onAddActivity,
                containerColor = Color(0xFF2563EB),
                contentColor   = Color.White,
                modifier       = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add Activity"
                )
            }
        }
    }
}

// ── Portfolio Score Card ──────────────────────────────────────────────────────
@Composable
private fun PortfolioScoreCard(
    score: Int,
    totalItems: Int,
    onViewGap: () -> Unit
) {
    val scoreColor = when {
        score >= 75 -> AccentGreen
        score >= 50 -> AccentAmber
        else        -> AccentRed
    }

    val scoreLabel = when {
        score >= 75 -> "Strong Portfolio 🔥"
        score >= 50 -> "Good Progress ⚡"
        score >= 25 -> "Keep Building 💪"
        else        -> "Just Getting Started 🌱"
    }

    // Animate score
    var animatedScore by remember { mutableStateOf(0f) }
    LaunchedEffect(score) {
        animate(
            initialValue  = 0f,
            targetValue   = score.toFloat(),
            animationSpec = tween(1000, easing = EaseOutCubic)
        ) { value, _ -> animatedScore = value }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(AccentBlue.copy(alpha = 0.5f), AccentPurple.copy(alpha = 0.3f))
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text          = "PORTFOLIO STRENGTH",
                    color         = TextSecondary,
                    fontSize      = 10.sp,
                    letterSpacing = 2.sp,
                    fontWeight    = FontWeight.SemiBold
                )
                Text(
                    text       = "${animatedScore.toInt()}%",
                    color      = scoreColor,
                    fontSize   = 48.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 52.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(scoreColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = scoreLabel,
                        color      = scoreColor,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = "$totalItems",
                    color      = AccentBlue,
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text     = "Activities",
                    color    = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BorderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(listOf(AccentBlue, AccentPurple))
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick  = onViewGap,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text     = "View Gap Analysis →",
                color    = AccentBlue,
                fontSize = 13.sp
            )
        }
    }
}

// ── Quick Actions ─────────────────────────────────────────────────────────────
@Composable
private fun QuickActionsRow(
    onAddActivity: () -> Unit,
    onViewPortfolio: () -> Unit,
    onViewAdvisor: () -> Unit,
    onViewGap: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionCard(
            emoji    = "➕",
            label    = "Add Activity",
            color    = AccentBlue,
            modifier = Modifier.weight(1f),
            onClick  = onAddActivity
        )
        QuickActionCard(
            emoji    = "📋",
            label    = "Portfolio",
            color    = AccentPurple,
            modifier = Modifier.weight(1f),
            onClick  = onViewPortfolio
        )
        QuickActionCard(
            emoji    = "🤖",
            label    = "AI Advisor",
            color    = AccentGreen,
            modifier = Modifier.weight(1f),
            onClick  = onViewAdvisor
        )
        QuickActionCard(
            emoji    = "📊",
            label    = "Gap Analysis",
            color    = AccentAmber,
            modifier = Modifier.weight(1f),
            onClick  = onViewGap
        )
    }
}

@Composable
private fun QuickActionCard(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Text(
            text       = label,
            color      = TextSecondary,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }
}

// ── Category Progress Grid ────────────────────────────────────────────────────
@Composable
private fun CategoryProgressGrid(
    categoryProgress: Map<PortfolioCategory, Int>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PortfolioCategory.entries.chunked(2).forEach { rowItems ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { category ->
                    val count = categoryProgress[category] ?: 0
                    CategoryProgressCard(
                        category = category,
                        count    = count,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space if odd number
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryProgressCard(
    category: PortfolioCategory,
    count: Int,
    modifier: Modifier
) {
    val hasItems = count > 0
    val color    = if (hasItems) AccentBlue else BorderSubtle

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(
                1.dp,
                color.copy(alpha = if (hasItems) 0.4f else 1f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = category.emoji, fontSize = 18.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = category.displayName,
                color      = if (hasItems) TextPrimary else TextSecondary,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = if (hasItems) "$count item${if (count > 1) "s" else ""}"
                else "Empty",
                color    = if (hasItems) AccentBlue else TextSecondary,
                fontSize = 10.sp
            )
        }
        if (hasItems) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentGreen)
            )
        }
    }
}

// ── Recent Activity Card ──────────────────────────────────────────────────────
@Composable
private fun RecentActivityCard(item: PortfolioItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item.category.emoji, fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = item.title,
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = item.category.displayName,
                color    = TextSecondary,
                fontSize = 12.sp
            )
        }
        if (item.isOngoing) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text     = "Ongoing",
                    color    = AccentGreen,
                    fontSize = 10.sp
                )
            }
        }
    }
}