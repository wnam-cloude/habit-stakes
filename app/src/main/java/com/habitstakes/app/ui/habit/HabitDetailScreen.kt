package com.habitstakes.app.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.ScreenshotMonitor
import androidx.compose.material.icons.filled.Edit as EditIcon
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitstakes.app.data.model.Completion
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.ProofType
import com.habitstakes.app.data.model.Stake
import com.habitstakes.app.data.model.StakeStatus
import com.habitstakes.app.ui.components.HabitCard
import com.habitstakes.app.ui.components.StakeStatusChip
import com.habitstakes.app.ui.theme.SurfaceContainer
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    viewModel: HabitDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Habit) -> Unit
) {
    val habit by viewModel.habit.collectAsStateWithLifecycle()
    val completions by viewModel.completions.collectAsStateWithLifecycle()
    val stakes by viewModel.stakes.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    habit?.let { h ->
        Scaffold(
            topBar = { HabitDetailTopBar(h, onBack, onEdit) },
            floatingActionButton = {
                if (h.isActive) {
                    ExtendedFloatingActionButton(
                        onClick = { showSubmitDialog(h, viewModel) },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Submit proof") },
                        text = { Text("Submit Proof") },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 8.dp)
            ) {
                // Habit header card
                HabitCard(
                    habit = com.habitstakes.app.data.model.HabitWithStats(h),
                    onClick = {}
                )

                // Stakes section
                if (stakes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    StakesSection(stakes)
                }

                // Completions history
                if (completions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CompletionsSection(completions)
                }
            }
        }
    }
}

@Composable
fun HabitDetailTopBar(
    habit: Habit,
    onBack: () -> Unit,
    onEdit: (Habit) -> Unit
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Back")
            }
        },
        title = {
            Text(text = habit.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        actions = {
            IconButton(onClick = { onEdit(habit) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit habit")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun StakesSection(stakes: List<Stake>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Stakes", style = MaterialTheme.typography.titleMedium)
            Text("${stakes.size} total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stakes) { stake ->
                StakeRow(stake)
            }
        }
    }
}

@Composable
fun StakeRow(stake: Stake) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Stake: \$${(stake.amount / 100).toInt()}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.W600)
                Text("Created: ${formatDate(stake.createdAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StakeStatusChip(status = stake.status)
        }
    }
}

@Composable
fun CompletionsSection(completions: List<Completion>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("History", style = MaterialTheme.typography.titleMedium)
            Text("${completions.size} entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(completions) { completion ->
                CompletionRow(completion)
            }
        }
    }
}

@Composable
fun CompletionRow(completion: Completion) {
    val isForfeit = completion.isForfeit
    val verified = completion.verified
    val bgColor = when {
        isForfeit -> Color(0xFFEF4444).copy(alpha = 0.1f)
        verified -> Color(0xFF10B981).copy(alpha = 0.1f)
        else -> Color(0xFFF59E0B).copy(alpha = 0.1f)
    }
    val textColor = when {
        isForfeit -> Color(0xFFEF4444)
        verified -> Color(0xFF10B981)
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = textColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isForfeit) "Forfeit: ${completion.forfeitReason ?: "No reason"}"
                           else "Proof: ${completion.proofType.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(formatDate(completion.submittedAt), style = MaterialTheme.typography.bodySmall)
            }
            if (completion.textNote != null && completion.textNote!!.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(completion.textNote!!, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.8f))
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SubmitProofDialog(
    habit: Habit,
    viewModel: HabitDetailViewModel,
    onDismiss: () -> Unit
) {
    var proofType by remember { mutableStateOf(habit.proofType) }
    var textNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit Proof for ${habit.title}") },
        text = {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth().widthIn(min = 280.dp)) {
                Text("Proof Type", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProofType.values().forEach { type ->
                        val selected = proofType == type
                        FilterChip(
                            selected = selected,
                            onClick = { proofType = type },
                            label = { Text(type.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                if (proofType == ProofType.TEXT) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = textNote,
                        onValueChange = { textNote = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.submitCompletion(
                        habit = habit,
                        proofUri = "mock://proof/\${System.currentTimeMillis()}",
                        proofType = proofType,
                        textNote = textNote.ifBlank { null }
                    )
                    onDismiss()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun showSubmitDialog(habit: Habit, viewModel: HabitDetailViewModel) {
    // Placeholder for real dialog host
}

private fun formatDate(instant: kotlinx.datetime.Instant): String =
    instant.toString().substring(0, 16).replace('T', ' ')
