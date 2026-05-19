package com.example.collegeadmitiq.ui.advisor

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegeadmitiq.domain.model.AISuggestion
import com.example.collegeadmitiq.domain.model.SuggestionDifficulty
import com.example.collegeadmitiq.domain.model.StudentProfile

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
fun AdvisorScreen(
    onBack: () -> Unit,
    viewModel: AdvisorViewModel = hiltViewModel()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint               = TextSecondary
                )
            }
            Column {
                Text(
                    text       = "AI Advisor 🤖",
                    color      = TextPrimary,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "Personalized activity suggestions",
                    color    = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        when {
            uiState.isGenerating -> {
                GeneratingState()
            }

            uiState.suggestions.isEmpty() -> {
                EmptyAdvisorState(
                    profile     = uiState.profile,
                    onGenerate  = { viewModel.generateSuggestions() }
                )
            }

            else -> {
                LazyColumn(
                    contentPadding      = PaddingValues(
                        horizontal = 20.dp,
                        vertical   = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile context card
                    item {
                        ProfileContextCard(profile = uiState.profile)
                    }

                    // Regenerate button
                    item {
                        OutlinedButton(
                            onClick  = { viewModel.generateSuggestions() },
                            modifier = Modifier.fillMaxWidth(),
                            border   = BorderStroke(1.dp, AccentBlue),
                            shape    = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Refresh,
                                contentDescription = null,
                                tint               = AccentBlue,
                                modifier           = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text     = "Generate New Suggestions",
                                color    = AccentBlue,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    // Error
                    if (uiState.error != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentRed.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text     = "⚠️ ${uiState.error}",
                                    color    = AccentRed,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Section label
                    item {
                        Text(
                            text          = "RECOMMENDED FOR YOU",
                            color         = TextSecondary,
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        )
                    }

                    // Suggestions
                    items(uiState.suggestions) { suggestion ->
                        SuggestionCard(
                            suggestion      = suggestion,
                            onToggleSaved   = { viewModel.toggleSaved(suggestion) },
                            onToggleComplete = { viewModel.toggleCompleted(suggestion) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Profile Context Card ──────────────────────────────────────────────────────
@Composable
private fun ProfileContextCard(profile: StudentProfile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(AccentBlue.copy(alpha = 0.4f), AccentPurple.copy(alpha = 0.2f))
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = "🎯", fontSize = 24.sp)
        Column {
            Text(
                text       = "Suggestions for ${profile?.name ?: "You"}",
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text     = "${profile?.intendedMajor ?: "Undecided"} • " +
                        "${profile?.grade ?: 9}th Grade • " +
                        "${profile?.dreamColleges?.firstOrNull() ?: "Dream College"}",
                color    = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

// ── Suggestion Card ───────────────────────────────────────────────────────────
@Composable
private fun SuggestionCard(
    suggestion: AISuggestion,
    onToggleSaved: () -> Unit,
    onToggleComplete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val difficultyColor = when (suggestion.difficulty) {
        SuggestionDifficulty.EASY   -> AccentGreen
        SuggestionDifficulty.MEDIUM -> AccentAmber
        SuggestionDifficulty.HARD   -> AccentRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (suggestion.isCompleted)
                    CardDark.copy(alpha = 0.5f)
                else CardDark
            )
            .border(
                1.dp,
                if (suggestion.isSaved)
                    Brush.horizontalGradient(
                        listOf(AccentBlue.copy(alpha = 0.5f), AccentPurple.copy(alpha = 0.3f))
                    )
                else
                    Brush.horizontalGradient(listOf(BorderSubtle, BorderSubtle)),
                RoundedCornerShape(16.dp)
            )
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        // Header row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(suggestion.category.let {
                            AccentBlue.copy(alpha = 0.15f)
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = suggestion.category.emoji,
                        fontSize = 20.sp
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = suggestion.title,
                        color      = if (suggestion.isCompleted)
                            TextSecondary else TextPrimary,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Difficulty badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(difficultyColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text     = "${suggestion.difficulty.emoji} ${suggestion.difficulty.displayName}",
                                color    = difficultyColor,
                                fontSize = 10.sp
                            )
                        }
                        // Time commitment
                        Text(
                            text     = suggestion.timeCommitment,
                            color    = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Action buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Save button
                IconButton(
                    onClick  = onToggleSaved,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (suggestion.isSaved)
                            Icons.Default.Bookmark
                        else
                            Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint               = if (suggestion.isSaved) AccentBlue
                        else TextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                // Complete button
                IconButton(
                    onClick  = onToggleComplete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (suggestion.isCompleted)
                            Icons.Default.CheckCircle
                        else
                            Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Complete",
                        tint               = if (suggestion.isCompleted) AccentGreen
                        else TextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Expanded content
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text       = suggestion.description,
                    color      = TextPrimary,
                    fontSize   = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Why relevant
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentBlue.copy(alpha = 0.1f))
                        .border(
                            1.dp,
                            AccentBlue.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text       = "💡 ${suggestion.whyRelevant}",
                        color      = AccentBlue,
                        fontSize   = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                // Deadline
                if (suggestion.deadline.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.DateRange,
                            contentDescription = null,
                            tint               = AccentAmber,
                            modifier           = Modifier.size(14.dp)
                        )
                        Text(
                            text     = "Deadline: ${suggestion.deadline}",
                            color    = AccentAmber,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Expand hint
        Text(
            text      = if (expanded) "▲ collapse" else "▼ tap to learn more",
            color     = TextSecondary,
            fontSize  = 10.sp,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

// ── Generating State ──────────────────────────────────────────────────────────
@Composable
private fun GeneratingState() {
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
                text       = "AI is analyzing your profile...",
                color      = TextPrimary,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text      = "Finding the best opportunities\nfor your college journey",
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
private fun EmptyAdvisorState(
    profile: StudentProfile?,
    onGenerate: () -> Unit
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
            Text(text = "🤖", fontSize = 56.sp)
            Text(
                text       = "Your AI Advisor is Ready!",
                color      = TextPrimary,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text      = "Get personalized activity suggestions\nbased on your major, interests,\nand dream colleges",
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick  = onGenerate,
                enabled  = profile != null,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = "✨ Generate My Suggestions",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}