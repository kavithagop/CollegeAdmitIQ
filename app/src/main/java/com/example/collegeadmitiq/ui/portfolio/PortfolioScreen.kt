package com.example.collegeadmitiq.ui.portfolio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegeadmitiq.domain.model.PortfolioCategory
import com.example.collegeadmitiq.domain.model.PortfolioItem

// ── Colors ────────────────────────────────────────────────────────────────────
private val BackgroundDark = Color(0xFF0A1628)  // Deep Navy
private val CardDark       = Color(0xFFE8F9EB)  // Slightly lighter navy
private val BorderSubtle   = Color(0xFF1E3A5F)  // Navy border
private val AccentBlue     = Color(0xFFF2FFF4)
private val AccentPurple   = Color(0xFF3D9900)
private val AccentGreen    = Color(0xFF1CB0F6)
private val TextPrimary    = Color(0xFF1A1A1A)
private val TextSecondary  = Color(0xFF6B7280)

@Composable
fun PortfolioScreen(
    onAddActivity: () -> Unit,
    onBack: () -> Unit,
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredItems = viewModel.filteredItems()

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
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        text       = "My Portfolio",
                        color      = TextPrimary,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = "${uiState.items.size} activities",
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            FloatingActionButton(
                onClick        = onAddActivity,
                containerColor = AccentBlue,
                contentColor   = Color.White,
                modifier       = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }

        // ── Category Filter ───────────────────────────────────────────────────
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All filter
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick  = { viewModel.selectCategory(null) },
                    label    = { Text("All (${uiState.items.size})") },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = AccentBlue,
                        selectedLabelColor        = Color.White,
                        containerColor            = CardDark,
                        labelColor                = TextSecondary
                    )
                )
            }
            items(PortfolioCategory.entries) { category ->
                val count = uiState.items.count { it.category == category }
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick  = { viewModel.selectCategory(category) },
                    label    = {
                        Text("${category.emoji} ${category.displayName} ($count)")
                    },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor     = Color.White,
                        containerColor         = CardDark,
                        labelColor             = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Items List ────────────────────────────────────────────────────────
        if (filteredItems.isEmpty()) {
            EmptyPortfolioState(
                category    = uiState.selectedCategory,
                onAddActivity = onAddActivity
            )
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(
                    horizontal = 20.dp,
                    vertical   = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems) { item ->
                    PortfolioItemCard(
                        item     = item,
                        onEdit   = { viewModel.showAddSheet(item) },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }
        }
    }
}

// ── Portfolio Item Card ───────────────────────────────────────────────────────
@Composable
private fun PortfolioItemCard(
    item: PortfolioItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(AccentBlue.copy(alpha = 0.3f), BorderSubtle)
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
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
                        .background(AccentBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.category.emoji, fontSize = 20.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = item.title,
                        color      = TextPrimary,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    if (item.organization.isNotBlank()) {
                        Text(
                            text     = item.organization,
                            color    = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector        = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint               = TextSecondary
                    )
                }
                DropdownMenu(
                    expanded         = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor   = CardDark
                ) {
                    DropdownMenuItem(
                        text    = { Text("Edit", color = TextPrimary) },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = AccentBlue
                            )
                        }
                    )
                    DropdownMenuItem(
                        text    = { Text("Delete", color = Color(0xFFFF4B4B)) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFFF4B4B)
                            )
                        }
                    )
                }
            }
        }

        if (item.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text       = item.description,
                color      = TextSecondary,
                fontSize   = 13.sp,
                lineHeight = 20.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tags row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Category tag
            TagChip(
                text  = item.category.displayName,
                color = AccentBlue
            )
            // Ongoing tag
            if (item.isOngoing) {
                TagChip(text = "Ongoing", color = AccentGreen)
            }
            // Hours tag
            if (item.hoursPerWeek > 0) {
                TagChip(
                    text  = "${item.hoursPerWeek}hrs/wk",
                    color = AccentPurple
                )
            }
        }
    }
}

// ── Tag Chip ──────────────────────────────────────────────────────────────────
@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text     = text,
            color    = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────
@Composable
private fun EmptyPortfolioState(
    category: PortfolioCategory?,
    onAddActivity: () -> Unit
) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Text(
                text     = category?.emoji ?: "📋",
                fontSize = 48.sp
            )
            Text(
                text       = if (category != null)
                    "No ${category.displayName} activities yet"
                else
                    "Your portfolio is empty",
                color      = TextPrimary,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text      = "Start adding your activities,\nawards, and experiences",
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddActivity,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text     = "Add First Activity",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}