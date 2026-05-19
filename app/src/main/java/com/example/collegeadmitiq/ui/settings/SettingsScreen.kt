package com.example.collegeadmitiq.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F1FF)  // Slightly lighter navy
private val BorderSubtle   = Color(0xFF1E3A5F)
private val AccentBlue     = Color(0xFFF0F6FF)
private val TextPrimary    = Color(0xFF0F172A)
private val TextSecondary  = Color(0xFF64748B)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

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
                Text(
                    text       = "Edit Profile",
                    color      = TextPrimary,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick  = {
                    android.util.Log.d("SettingsDebug", "=== SAVE CLICKED ===")
                    android.util.Log.d("SettingsDebug", "name: '${uiState.name}'")
                    android.util.Log.d("SettingsDebug", "major: '${uiState.intendedMajor}'")
                    android.util.Log.d("SettingsDebug", "isValid: ${viewModel.isValid()}")
                    android.util.Log.d("SettingsDebug", "isSaving: ${uiState.isSaving}")
                    viewModel.saveProfile()
                           },
                enabled  = viewModel.isValid() && !uiState.isSaving,
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = AccentBlue,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.3f)
                ),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(16.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Save", color = Color.White)
                }
            }
        }

        // ── Form ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Basic Info ────────────────────────────────────────────────────
            SectionHeader(title = "Basic Info")

            SettingsTextField(
                value       = uiState.name,
                onValue     = { viewModel.onNameChanged(it) },
                label       = "Full Name *",
                placeholder = "e.g. Alex Johnson"
            )

            // Grade selector
            Text(
                text     = "Current Grade",
                color    = TextSecondary,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(9, 10, 11, 12).forEach { g ->
                    val selected = uiState.grade == g
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) AccentBlue else CardDark
                            )
                            .border(
                                1.dp,
                                if (selected) AccentBlue else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.onGradeChanged(g) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "${g}th",
                            color      = if (selected) Color.White else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold
                            else FontWeight.Normal,
                            fontSize   = 14.sp
                        )
                    }
                }
            }

            SettingsTextField(
                value        = uiState.gpa,
                onValue      = { viewModel.onGpaChanged(it) },
                label        = "GPA (optional)",
                placeholder  = "e.g. 3.8",
                keyboardType = KeyboardType.Decimal
            )

            SettingsTextField(
                value       = uiState.intendedMajor,
                onValue     = { viewModel.onMajorChanged(it) },
                label       = "Intended Major *",
                placeholder = "e.g. Computer Science"
            )

            // ── Test Scores ───────────────────────────────────────────────────
            SectionHeader(title = "Test Scores")

            // SAT + ACT side by side
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    SettingsTextField(
                        value        = uiState.satScore,
                        onValue      = { viewModel.onSatChanged(it) },
                        label        = "SAT Score",
                        placeholder  = "400–1600",
                        keyboardType = KeyboardType.Number
                    )
                    Text(
                        text     = "Max: 1600",
                        color    = TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    SettingsTextField(
                        value        = uiState.actScore,
                        onValue      = { viewModel.onActChanged(it) },
                        label        = "ACT Score",
                        placeholder  = "1–36",
                        keyboardType = KeyboardType.Number
                    )
                    Text(
                        text     = "Max: 36",
                        color    = TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }

            // SAT score guide
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlue.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        AccentBlue.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text       = "💡 These scores help AI give more accurate college match suggestions",
                    color      = AccentBlue,
                    fontSize   = 12.sp,
                    lineHeight = 18.sp
                )
            }

            // ── Interests ─────────────────────────────────────────────────────
            SectionHeader(title = "Interests")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SettingsTextField(
                    value       = uiState.currentInterest,
                    onValue     = { viewModel.onInterestChanged(it) },
                    label       = "Add Interest",
                    placeholder = "e.g. Robotics",
                    modifier    = Modifier.weight(1f),
                    onDone      = { viewModel.addInterest() }
                )
                IconButton(
                    onClick  = { viewModel.addInterest() },
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

            if (uiState.interests.isNotEmpty()) {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.interests.forEach { interest ->
                        EditableChip(
                            text     = interest,
                            onRemove = { viewModel.removeInterest(interest) }
                        )
                    }
                }
            }

            // ── Dream Colleges ────────────────────────────────────────────────
            SectionHeader(title = "Dream Colleges")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                SettingsTextField(
                    value       = uiState.currentCollege,
                    onValue     = { viewModel.onCollegeChanged(it) },
                    label       = "Add College",
                    placeholder = "e.g. MIT, Stanford",
                    modifier    = Modifier.weight(1f),
                    onDone      = { viewModel.addCollege() }
                )
                IconButton(
                    onClick  = { viewModel.addCollege() },
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

            if (uiState.dreamColleges.isNotEmpty()) {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.dreamColleges.forEach { college ->
                        EditableChip(
                            text     = college,
                            onRemove = { viewModel.removeCollege(college) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String) {
    Column {
        HorizontalDivider(color = BorderSubtle)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text          = title.uppercase(),
            color         = AccentBlue,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Settings TextField ────────────────────────────────────────────────────────
@Composable
private fun SettingsTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValue,
        modifier      = modifier,
        label         = { Text(label, color = TextSecondary) },
        placeholder   = {
            Text(
                placeholder,
                color = TextSecondary.copy(alpha = 0.5f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
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

// ── Editable Chip ─────────────────────────────────────────────────────────────
@Composable
private fun EditableChip(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AccentBlue.copy(alpha = 0.15f))
            .border(
                1.dp,
                AccentBlue.copy(alpha = 0.4f),
                RoundedCornerShape(20.dp)
            )
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