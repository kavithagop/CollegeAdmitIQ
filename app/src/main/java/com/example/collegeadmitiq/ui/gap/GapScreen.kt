package com.example.collegeadmitiq.ui.gap

import CategoryScore
import GapAnalysis
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F9EB)  // Slightly lighter navy
private val BorderSubtle   = Color(0xFF1E3A5F)
private val AccentBlue     = Color(0xFFF2FFF4)
private val AccentPurple   = Color(0xFF3D9900)
private val AccentGreen    = Color(0xFF1CB0F6)
private val AccentAmber    = Color(0xFFFF9600)
private val AccentRed      = Color(0xFFFF4B4B)
private val TextPrimary    = Color(0xFF1A1A1A)
private val TextSecondary  = Color(0xFF6B7280)

@Composable
fun GapScreen(
    onBack: () -> Unit,
    viewModel: GapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint               = TextSecondary
                    )
                }
                Column {
                    Text(
                        text       = "Gap Analysis 📊",
                        color      = TextPrimary,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = "See what's missing in your portfolio",
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            if (uiState.gapAnalysis != null) {
                IconButton(onClick = { viewModel.analyzeGaps() }) {
                    Icon(
                        imageVector        = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint               = AccentBlue
                    )
                }
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        when {
            uiState.isAnalyzing -> {
                AnalyzingState()
            }

            uiState.gapAnalysis != null -> {
                GapAnalysisContent(
                    analysis  = uiState.gapAnalysis!!,
                    onRefresh = { viewModel.analyzeGaps() }
                )
            }

            else -> {
                EmptyGapState(
                    error      = uiState.error,
                    onAnalyze  = { viewModel.analyzeGaps() }
                )
            }
        }
    }
}

// ── Gap Analysis Content ──────────────────────────────────────────────────────
@Composable
private fun GapAnalysisContent(
    analysis: GapAnalysis,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Overall score card
        OverallScoreCard(analysis = analysis)

        Spacer(modifier = Modifier.height(20.dp))

        // Strengths
        if (analysis.strengths.isNotEmpty()) {
            SectionLabel("YOUR STRENGTHS 💪")
            Spacer(modifier = Modifier.height(8.dp))
            analysis.strengths.forEach { strength ->
                BulletCard(
                    text  = strength,
                    color = AccentGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Top priorities
        if (analysis.topPriorities.isNotEmpty()) {
            SectionLabel("TOP PRIORITIES 🎯")
            Spacer(modifier = Modifier.height(8.dp))
            analysis.topPriorities.forEachIndexed { index, priority ->
                PriorityCard(
                    number   = index + 1,
                    text     = priority
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category breakdown
        SectionLabel("CATEGORY BREAKDOWN")
        Spacer(modifier = Modifier.height(8.dp))

        analysis.categoryScores.values
            .sortedBy { it.score }
            .forEach { categoryScore ->
                CategoryScoreCard(categoryScore = categoryScore)
                Spacer(modifier = Modifier.height(8.dp))
            }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Overall Score Card ────────────────────────────────────────────────────────
@Composable
private fun OverallScoreCard(analysis: GapAnalysis) {
    val scoreColor = when {
        analysis.overallScore >= 75 -> AccentGreen
        analysis.overallScore >= 50 -> AccentAmber
        else                        -> AccentRed
    }

    var animatedScore by remember { mutableStateOf(0f) }
    LaunchedEffect(analysis.overallScore) {
        animate(
            initialValue  = 0f,
            targetValue   = analysis.overallScore.toFloat(),
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
                    listOf(scoreColor.copy(alpha = 0.5f), BorderSubtle)
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text          = "OVERALL PORTFOLIO SCORE",
            color         = TextSecondary,
            fontSize      = 11.sp,
            letterSpacing = 2.sp,
            fontWeight    = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text       = "${animatedScore.toInt()}",
            color      = scoreColor,
            fontSize   = 72.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 72.sp
        )
        Text(
            text     = "out of 100",
            color    = TextSecondary,
            fontSize = 14.sp
        )
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
                    .fillMaxWidth(analysis.overallScore / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentBlue, scoreColor)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text       = analysis.summary,
            color      = TextPrimary,
            fontSize   = 14.sp,
            lineHeight = 22.sp,
            textAlign  = TextAlign.Center
        )
    }
}

// ── Category Score Card ───────────────────────────────────────────────────────
@Composable
private fun CategoryScoreCard(categoryScore: CategoryScore) {
    val statusColor = when (categoryScore.status) {
        GapStatus.STRONG  -> AccentGreen
        GapStatus.GOOD    -> AccentBlue
        GapStatus.WEAK    -> AccentAmber
        GapStatus.MISSING -> AccentRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(
                1.dp,
                statusColor.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text     = categoryScore.category.emoji,
                    fontSize = 20.sp
                )
                Column {
                    Text(
                        text       = categoryScore.category.displayName,
                        color      = TextPrimary,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text     = "${categoryScore.itemCount} item${if (categoryScore.itemCount != 1) "s" else ""}",
                        color    = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text       = "${categoryScore.status.emoji} ${categoryScore.status.displayName}",
                    color      = statusColor,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Score bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(BorderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(categoryScore.score / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(statusColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text       = categoryScore.feedback,
            color      = TextSecondary,
            fontSize   = 12.sp,
            lineHeight = 18.sp
        )
    }
}

// ── Bullet Card ───────────────────────────────────────────────────────────────
@Composable
private fun BulletCard(text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text       = text,
            color      = TextPrimary,
            fontSize   = 13.sp,
            lineHeight = 20.sp
        )
    }
}

// ── Priority Card ─────────────────────────────────────────────────────────────
@Composable
private fun PriorityCard(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AccentAmber.copy(alpha = 0.08f))
            .border(1.dp, AccentAmber.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AccentAmber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "$number",
                color      = AccentAmber,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text       = text,
            color      = TextPrimary,
            fontSize   = 13.sp,
            lineHeight = 20.sp,
            modifier   = Modifier.weight(1f)
        )
    }
}

// ── Section Label ─────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text,
        color         = TextSecondary,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 2.sp
    )
}

// ── Analyzing State ───────────────────────────────────────────────────────────
@Composable
private fun AnalyzingState() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(color = AccentBlue)
            Text(
                text       = "Analyzing your portfolio...",
                color      = TextPrimary,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text      = "AI is comparing your activities\nagainst your dream colleges",
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────
@Composable
private fun EmptyGapState(
    error: String?,
    onAnalyze: () -> Unit
) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Text(text = "📊", fontSize = 56.sp)
            Text(
                text       = "Analyze Your Portfolio",
                color      = TextPrimary,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text      = "AI will identify gaps in your portfolio\nand tell you exactly what's missing\nfor your dream colleges",
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentRed.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        text     = "⚠️ $error",
                        color    = AccentRed,
                        fontSize = 13.sp
                    )
                }
            }
            Button(
                onClick  = onAnalyze,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = "✨ Analyze My Portfolio",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}