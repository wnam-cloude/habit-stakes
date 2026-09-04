package com.habitstakes.app.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitstakes.app.data.model.HabitWithStats
import com.habitstakes.app.ui.components.HabitCard
import com.habitstakes.app.ui.theme.HabitStakesTheme
import com.habitstakes.app.ui.theme.SurfaceContainer
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onHabitClick: (HabitWithStats) -> Unit,
    onCreateHabit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.BottomEnd
    ) {
        androidx.compose.material3.Scaffold(
            topBar = { HomeTopBar(uiState.profile) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onCreateHabit,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add habit") },
                    text = { Text("New Habit") },
                    modifier = Modifier.padding(16.dp)
                )
            },
            floatingActionButtonPosition = androidx.compose.material3.FabPosition.End
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 8.dp)
            ) {
                // Stats summary
                if (uiState.profile != null) {
                    StatsSummaryCard(uiState.profile!!)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                }

                // Habits list
                if (uiState.activeHabits.isEmpty()) {
                    EmptyState(onCreateHabit = onCreateHabit)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 0.dp, 16.dp, 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.activeHabits) { habit ->
                            HabitCard(
                                habit = habit,
                                onClick = { onHabitClick(habit) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(profile: com.habitstakes.app.data.model.UserProfile?) {
    androidx.compose.material3.TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = "Habit Stakes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            if (profile != null) {
                androidx.compose.material3.IconButton(onClick = { /* TODO: Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun StatsSummaryCard(profile: com.habitstakes.app.data.model.UserProfile) {
    SurfaceContainer {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your Scorecard", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatCard("Total Staked", formatCurrency(profile.totalStaked), Icons.Default.AttachMoney, Color(0xFF10B981))
                StatCard("Total Won", formatCurrency(profile.totalWon), Icons.Default.TrendingUp, Color(0xFF3B82F6))
                StatCard("Forfeited", formatCurrency(profile.totalForfeited), Icons.Default.Wallet, Color(0xFFEF4444))
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(16.dp)
            .background(
                color = color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyState(onCreateHabit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        Text("No habits yet", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        Text("Create your first habit stake and start building momentum", 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateHabit, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text("Create Habit", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

private fun formatCurrency(cents: Double): String = "\$${(cents / 100).toInt()}"
