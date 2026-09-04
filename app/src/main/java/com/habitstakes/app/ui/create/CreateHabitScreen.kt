package com.habitstakes.app.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.ScreenshotMonitor
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitstakes.app.data.model.Frequency
import com.habitstakes.app.data.model.ProofType
import com.habitstakes.app.data.model.StakeType
import com.habitstakes.app.ui.theme.SurfaceContainer

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title field
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Habit Title", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text("e.g., Wake up at 6 AM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Description (optional)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Stake Type Selector
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Stake Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Stake Amount (only for money stake)
            if (stakeType == StakeType.MONEY) {
                SurfaceContainer {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stake Amount (USD)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = stakeAmount,
                            onValueChange = { viewModel.updateStakeAmount(it) },
                            placeholder = { Text("e.g., 10.00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = { Text("\$", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp)) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("You'll forfeit this amount if you don't complete the habit", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Frequency
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Frequency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder Time
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reminder Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    TimePickerField(
                        time = reminderTime,
                        onTimeChange = { viewModel.updateReminderTime(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Proof Type
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Proof Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced options
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Advanced", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Grace period
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grace Period", style = MaterialTheme.typography.bodyMedium)
                            Text("${gracePeriod} min after reminder", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Slider(
                            value = gracePeriod.toFloat(),
                            onValueChange = { viewModel.updateGracePeriod(it.toInt()) },
                            valueRange = 0f..120f,
                            steps = 12,
                            modifier = Modifier.width(150.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Auto-verify
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Verify", style = MaterialTheme.typography.bodyMedium)
                            Text("AI-based verification", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoVerify,
                            onCheckedChange = { viewModel.updateAutoVerify(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            SurfaceContainer {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tags (comma-separated)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
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
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    val id = viewModel.createHabit()
                    // Create button doesn't return ID directly due to async, 
                    // it's handled via uiState.createdHabitId above.
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = uiState.isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Create Habit", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .height(90.dp)
            .width(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) color else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(type.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyChip(
    frequency: Frequency,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(56.dp)
            .width(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(frequency.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProofTypeChip(
    type: ProofType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(64.dp)
            .width(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(type.displayName, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TimePickerField(
    time: String,
    onTimeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = time,
            onValueChange = onTimeChange,
            label = { Text("HH:MM") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("24h format", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
