package com.habitstakes.app.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitstakes.app.data.model.Frequency
import com.habitstakes.app.data.model.ProofType
import com.habitstakes.app.data.model.StakeType
import com.habitstakes.app.ui.theme.HabitStakesTheme
import com.habitstakes.app.ui.theme.SurfaceContainer
import kotlinx.coroutines.launch

@Composable
fun CreateHabitScreen(
    viewModel: CreateHabitViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onHabitCreated: (Long) -> Unit
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val stakeType by viewModel.stakeType.collectAsStateWithLifecycle()
    val stakeAmount by viewModel.stakeAmount.collectAsStateWithLifecycle()
    val frequency by viewModel.frequency.collectAsStateWithLifecycle()
    val reminderTime by viewModel.reminderTime.collectAsStateWithLifecycle()
    val proofType by viewModel.proofType.collectAsStateWithLifecycle()
    val gracePeriod by viewModel.gracePeriod.collectAsStateWithLifecycle()
    val autoVerify by viewModel.autoVerify.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("New Habit", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    if (uiState.createdHabitId != null) {
                        IconButton(onClick = { viewModel.resetForm(); onHabitCreated(uiState.createdHabitId!!) }) {
                            Icon(Icons.Default.Check, contentDescription = "Done")
                        }
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title field
            SurfaceContainer {
                Column {
                    Text("Habit Title", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text("e.g., Wake up at 6 AM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Description
            SurfaceContainer {
                Column {
                    Text("Description (optional)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = description,
                        onValueChange = { viewModel.updateDescription(it) },
                        placeholder = { Text("Why does this matter to you?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        singleLine = false
                    )
                }
            }

            // Stake Type Selector
            SurfaceContainer {
                Column {
                    Text("Stake Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StakeType.values().forEach { type ->
                            StakeTypeChip(
                                type = type,
                                selected = stakeType == type,
                                onClick = { viewModel.updateStakeType(type) }
                            )
                        }
                    }
                }
            }

            // Stake Amount (only for money stake)
            if (stakeType == StakeType.MONEY) {
                SurfaceContainer {
                    Column {
                        Text("Stake Amount (USD)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = stakeAmount,
                            onValueChange = { viewModel.updateStakeAmount(it) },
                            placeholder = { Text("e.g., 10.00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            leadingIcon = { Text("\$", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp)) }
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                        Text("You'll forfeit this amount if you don't complete the habit", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Frequency
            SurfaceContainer {
                Column {
                    Text("Frequency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Frequency.values().forEach { freq ->
                            FrequencyChip(
                                frequency = freq,
                                selected = frequency == freq,
                                onClick = { viewModel.updateFrequency(freq) }
                            )
                        }
                    }
                }
            }

            // Reminder Time
            SurfaceContainer {
                Column {
                    Text("Reminder Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    TimePickerField(
                        time = reminderTime,
                        onTimeChange = { viewModel.updateReminderTime(it) }
                    )
                }
            }

            // Proof Type
            SurfaceContainer {
                Column {
                    Text("Proof Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProofType.values().forEach { type ->
                            ProofTypeChip(
                                type = type,
                                selected = proofType == type,
                                onClick = { viewModel.updateProofType(type) }
                            )
                        }
                    }
                }
            }

            // Advanced options
            SurfaceContainer {
                Column {
                    Text("Advanced", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

                    // Grace period
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Grace Period", style = MaterialTheme.typography.bodyMedium)
                            Text("${gracePeriod} minutes after reminder", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        androidx.compose.material3.Slider(
                            value = gracePeriod.toFloat(),
                            onValueChange = { viewModel.updateGracePeriod(it.toInt()) },
                            valueRange = 0f..120f,
                            steps = 12,
                            modifier = Modifier.width(200.dp)
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                    // Auto-verify
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Auto-Verify", style = MaterialTheme.typography.bodyMedium)
                            Text("Automatically approve submissions (AI)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        androidx.compose.material3.Switch(
                            checked = autoVerify,
                            onCheckedChange = { viewModel.updateAutoVerify(it) }
                        )
                    }
                }
            }

            // Tags
            SurfaceContainer {
                Column {
                    Text("Tags (comma-separated)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = tags,
                        onValueChange = { viewModel.updateTags(it) },
                        placeholder = { Text("morning, fitness, health") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Create button
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    val id = viewModel.createHabit()
                    id?.let { onHabitCreated(it) }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                enabled = uiState.isValid,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Create Habit", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StakeTypeChip(
    type: StakeType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = when (type) {
        StakeType.MONEY -> Color(0xFF10B981)
        StakeType.SOCIAL -> Color(0xFF3B82F6)
        StakeType.TIME -> Color(0xFF8B5CF6)
        StakeType.COMBO -> Color(0xFFEC4899)
    }

    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) color else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (type) {
                    StakeType.MONEY -> Icons.Default.AttachMoney
                    StakeType.SOCIAL -> Icons.Default.People
                    StakeType.TIME -> Icons.Default.Timer
                    StakeType.COMBO -> Icons.Default.AutoAwesome
                },
                contentDescription = null,
                tint = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(type.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(type.description, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
fun FrequencyChip(
    frequency: Frequency,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(72.dp)
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(frequency.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProofTypeChip(
    type: ProofType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(72.dp)
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (type) {
                    ProofType.PHOTO -> Icons.Default.PhotoCamera
                    ProofType.VIDEO -> Icons.Default.Videocam
                    ProofType.TIMELAPSE -> Icons.Default.Timelapse
                    ProofType.SCREENSHOT -> Icons.Default.ScreenshotMonitor
                    ProofType.TEXT -> Icons.Default.Edit
                    ProofType.GPS -> Icons.Default.LocationOn
                    ProofType.BIOMETRIC -> Icons.Default.Fingerprint
                },
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            Text(type.displayName, style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.TextAlign.Center)
        }
    }
}

@Composable
fun TimePickerField(
    time: String,
    onTimeChange: (String) -> Unit
) {
    // Simple time input - in production would use TimePickerDialog
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            label = { Text("HH:MM") },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        Text("24h format", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterVertically))
    }
}
