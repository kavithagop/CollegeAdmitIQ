package com.example.collegeadmitiq.ui.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F1FF)  // Slightly lighter navy
private val AccentBlue     = Color(0xFFF0F6FF)
private val AccentPurple   = Color(0xFF7C3AED)
private val TextPrimary    = Color(0xFF0F172A)
private val TextSecondary  = Color(0xFF64748B)
private val BorderSubtle   = Color(0xFF1E3A5F)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text       = "CollegeAdmitIQ",
                color      = AccentBlue,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text       = "Let's build your profile 🎓",
                color      = TextPrimary,
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            val progress = (uiState.currentStep + 1).toFloat() / uiState.totalSteps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AccentBlue, AccentPurple)
                            )
                        )
                )
            }

            Text(
                text     = "Step ${uiState.currentStep + 1} of ${uiState.totalSteps}",
                color    = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                when (uiState.currentStep) {
                    0 -> StepBasicInfo(
                        name     = uiState.name,
                        grade    = uiState.grade,
                        gpa      = uiState.gpa,
                        onName   = { viewModel.onNameChanged(it) },
                        onGrade  = { viewModel.onGradeChanged(it) },
                        onGpa    = { viewModel.onGpaChanged(it) }
                    )
                    1 -> StepMajor(
                        major    = uiState.intendedMajor,
                        onMajor  = { viewModel.onMajorChanged(it) }
                    )
                    2 -> StepInterests(
                        interests       = uiState.interests,
                        currentInterest = uiState.currentInterest,
                        onInterest      = { viewModel.onInterestChanged(it) },
                        onAdd           = { viewModel.addInterest() },
                        onRemove        = { viewModel.removeInterest(it) }
                    )
                    3 -> StepDreamColleges(
                        colleges       = uiState.dreamColleges,
                        currentCollege = uiState.currentCollege,
                        onCollege      = { viewModel.onCollegeChanged(it) },
                        onAdd          = { viewModel.addCollege() },
                        onRemove       = { viewModel.removeCollege(it) }
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.currentStep > 0) {
                    OutlinedButton(
                        onClick  = { viewModel.previousStep() },
                        modifier = Modifier.weight(1f),
                        border   = BorderStroke(1.dp, BorderSubtle),
                        shape    = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text     = "Back",
                            color    = TextSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                Button(
                    onClick  = { viewModel.nextStep() },
                    enabled  = viewModel.isCurrentStepValid() && !uiState.isSaving,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = AccentBlue,
                        disabledContainerColor = AccentBlue.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text       = if (uiState.currentStep == uiState.totalSteps - 1)
                                "Let's Go! 🚀" else "Next →",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Step 1 — Basic Info ───────────────────────────────────────────────────────
@Composable
private fun StepBasicInfo(
    name: String,
    grade: Int,
    gpa: String,
    onName: (String) -> Unit,
    onGrade: (Int) -> Unit,
    onGpa: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text       = "Tell us about yourself",
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Name input
        OnboardingTextField(
            value       = name,
            onValue     = onName,
            label       = "Your Name",
            placeholder = "e.g. Alex Johnson"
        )

        // Grade selector
        Column {
            Text(
                text     = "Current Grade",
                color    = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(9, 10, 11, 12).forEach { g ->
                    val selected = grade == g
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) AccentBlue
                                else CardDark
                            )
                            .border(
                                1.dp,
                                if (selected) AccentBlue else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onGrade(g) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "${g}th",
                            color      = if (selected) Color.White else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize   = 14.sp
                        )
                    }
                }
            }
        }

        // GPA input
        OnboardingTextField(
            value         = gpa,
            onValue       = onGpa,
            label         = "Current GPA (optional)",
            placeholder   = "e.g. 3.8",
            keyboardType  = KeyboardType.Decimal
        )
    }
}

// ── Step 2 — Major ────────────────────────────────────────────────────────────
@Composable
private fun StepMajor(
    major: String,
    onMajor: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text       = "What do you want to study?",
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text     = "This helps us suggest the most relevant activities for your goals",
            color    = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        OnboardingTextField(
            value       = major,
            onValue     = onMajor,
            label       = "Intended Major",
            placeholder = "e.g. Computer Science, Pre-Med, Business"
        )

        // Quick suggestions
        Text(
            text     = "Popular choices:",
            color    = TextSecondary,
            fontSize = 12.sp
        )
        val suggestions = listOf(
            "Computer Science", "Pre-Med", "Business",
            "Engineering", "Psychology", "Law", "Education", "Arts"
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (major == suggestion) AccentBlue.copy(alpha = 0.2f)
                            else CardDark
                        )
                        .border(
                            1.dp,
                            if (major == suggestion) AccentBlue else BorderSubtle,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onMajor(suggestion) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text     = suggestion,
                        color    = if (major == suggestion) AccentBlue else TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ── Step 3 — Interests ────────────────────────────────────────────────────────
@Composable
private fun StepInterests(
    interests: List<String>,
    currentInterest: String,
    onInterest: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text       = "What are your interests?",
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text     = "Add things you enjoy — hobbies, subjects, passions",
            color    = TextSecondary,
            fontSize = 14.sp
        )

        // Input row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OnboardingTextField(
                value       = currentInterest,
                onValue     = onInterest,
                label       = "Add Interest",
                placeholder = "e.g. Robotics, Soccer, Music",
                modifier    = Modifier.weight(1f),
                onDone      = onAdd
            )
            IconButton(
                onClick  = onAdd,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlue)
                    .size(52.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add",
                    tint               = Color.White
                )
            }
        }

        // Added interests
        if (interests.isNotEmpty()) {
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                interests.forEach { interest ->
                    ChipItem(
                        text     = interest,
                        onRemove = { onRemove(interest) }
                    )
                }
            }
        }
    }
}

// ── Step 4 — Dream Colleges ───────────────────────────────────────────────────
@Composable
private fun StepDreamColleges(
    colleges: List<String>,
    currentCollege: String,
    onCollege: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text       = "What are your dream colleges?",
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text     = "Add 1-5 colleges you'd love to attend",
            color    = TextSecondary,
            fontSize = 14.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OnboardingTextField(
                value       = currentCollege,
                onValue     = onCollege,
                label       = "Add College",
                placeholder = "e.g. MIT, Stanford, UCLA",
                modifier    = Modifier.weight(1f),
                onDone      = onAdd
            )
            IconButton(
                onClick  = onAdd,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlue)
                    .size(52.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add",
                    tint               = Color.White
                )
            }
        }

        if (colleges.isNotEmpty()) {
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                colleges.forEach { college ->
                    ChipItem(
                        text     = college,
                        onRemove = { onRemove(college) }
                    )
                }
            }
        }
    }
}

// ── Shared Composable ────────────────────────────────────────────────────────
@Composable
private fun OnboardingTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValue,
        modifier      = modifier.fillMaxWidth(),
        label         = { Text(label, color = TextSecondary) },
        placeholder   = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentBlue,
            unfocusedBorderColor = BorderSubtle,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            cursorColor          = AccentBlue,
            focusedLabelColor    = AccentBlue
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction    = if (onDone != null)
                androidx.compose.ui.text.input.ImeAction.Done
            else
                androidx.compose.ui.text.input.ImeAction.Next
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onDone = { onDone?.invoke() }
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ChipItem(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AccentBlue.copy(alpha = 0.15f))
            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text     = text,
            color    = AccentBlue,
            fontSize = 13.sp
        )
        IconButton(
            onClick  = onRemove,
            modifier = Modifier.size(18.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Remove",
                tint               = AccentBlue,
                modifier           = Modifier.size(14.dp)
            )
        }
    }
}