package com.example.collegeadmitiq.ui.portfolio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F1FF)  // Slightly lighter navy
private val BorderSubtle   = Color(0xFF1E3A5F)
private val AccentBlue     = Color(0xFFF0F6FF)
private val TextPrimary    = Color(0xFF0F172A)
private val TextSecondary  = Color(0xFF64748B)

@Composable
fun AddActivityScreen(
    editingItem: PortfolioItem? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddActivityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load editing item if provided
    LaunchedEffect(editingItem) {
        editingItem?.let { viewModel.loadItem(it) }
    }

    // Navigate back when saved
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
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
                    text       = if (editingItem != null) "Edit Activity"
                    else "Add Activity",
                    color      = TextPrimary,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick  = { viewModel.saveActivity(editingItem?.id ?: 0L) },
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
                    Text(
                        text  = "Save",
                        color = Color.White
                    )
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

            // Category selector
            SectionLabel("Category")
            CategorySelector(
                selected = uiState.category,
                onSelect = { viewModel.onCategoryChanged(it) }
            )

            // Title
            ActivityTextField(
                value       = uiState.title,
                onValue     = { viewModel.onTitleChanged(it) },
                label       = "Activity Title *",
                placeholder = "e.g. Varsity Soccer Captain"
            )

            // Organization
            ActivityTextField(
                value       = uiState.organization,
                onValue     = { viewModel.onOrganizationChanged(it) },
                label       = "Organization / School",
                placeholder = "e.g. Lincoln High School"
            )

            // Role
            ActivityTextField(
                value       = uiState.role,
                onValue     = { viewModel.onRoleChanged(it) },
                label       = "Your Role",
                placeholder = "e.g. Team Captain, President, Volunteer"
            )

            // Description
            ActivityTextField(
                value       = uiState.description,
                onValue     = { viewModel.onDescriptionChanged(it) },
                label       = "Description *",
                placeholder = "Describe what you did and accomplished",
                minLines    = 3
            )

            // Impact
            ActivityTextField(
                value       = uiState.impact,
                onValue     = { viewModel.onImpactChanged(it) },
                label       = "Impact / Achievement",
                placeholder = "e.g. Led team to state championship",
                minLines    = 2
            )

            // Hours
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityTextField(
                    value        = uiState.hoursPerWeek,
                    onValue      = { viewModel.onHoursChanged(it) },
                    label        = "Hours/Week",
                    placeholder  = "e.g. 10",
                    keyboardType = KeyboardType.Number,
                    modifier     = Modifier.weight(1f)
                )
                ActivityTextField(
                    value        = uiState.weeksPerYear,
                    onValue      = { viewModel.onWeeksChanged(it) },
                    label        = "Weeks/Year",
                    placeholder  = "e.g. 40",
                    keyboardType = KeyboardType.Number,
                    modifier     = Modifier.weight(1f)
                )
            }

            // Ongoing toggle
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Currently Ongoing",
                    color    = TextPrimary,
                    fontSize = 14.sp
                )
                Switch(
                    checked         = uiState.isOngoing,
                    onCheckedChange = { viewModel.onOngoingChanged(it) },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor  = Color.White,
                        checkedTrackColor  = AccentBlue
                    )
                )
            }

            // Grade selector
            SectionLabel("Active During Grade(s)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(9, 10, 11, 12).forEach { grade ->
                    val selected = when (grade) {
                        9  -> uiState.grade9
                        10 -> uiState.grade10
                        11 -> uiState.grade11
                        12 -> uiState.grade12
                        else -> false
                    }
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
                            .clickable { viewModel.onGradeToggled(grade) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "${grade}th",
                            color      = if (selected) Color.White
                            else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold
                            else FontWeight.Normal,
                            fontSize   = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Category Selector ─────────────────────────────────────────────────────────
@Composable
private fun CategorySelector(
    selected: PortfolioCategory,
    onSelect: (PortfolioCategory) -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement   = Arrangement.spacedBy(8.dp)
    ) {
        PortfolioCategory.entries.forEach { category ->
            val isSelected = category == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) AccentBlue.copy(alpha = 0.2f)
                        else CardDark
                    )
                    .border(
                        1.dp,
                        if (isSelected) AccentBlue else BorderSubtle,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(category) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text     = "${category.emoji} ${category.displayName}",
                    color    = if (isSelected) AccentBlue else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold
                    else FontWeight.Normal
                )
            }
        }
    }
}

// ── Shared Composables ────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text,
        color         = TextSecondary,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )
}

@Composable
private fun ActivityTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
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
        minLines      = minLines,
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentBlue,
            unfocusedBorderColor = BorderSubtle,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            cursorColor          = AccentBlue,
            focusedLabelColor    = AccentBlue
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape           = RoundedCornerShape(12.dp)
    )
}