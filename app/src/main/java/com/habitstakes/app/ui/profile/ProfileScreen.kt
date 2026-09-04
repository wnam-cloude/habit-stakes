package com.habitstakes.app.ui.profile

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.habitstakes.app.data.model.UserProfile
import com.habitstakes.app.ui.theme.HabitStakesTheme
import com.habitstakes.app.ui.theme.SurfaceContainer
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    profile?.let { p ->
        androidx.compose.material3.Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text("Profile", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        IconButton(onClick = { /* TODO: Settings */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                // Profile header
                ProfileHeader(p)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                // Stats cards
                StatsGrid(p)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                // Settings
                SettingsSection(viewModel, p)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                // Danger zone
                DangerZone(viewModel, uiState)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    SurfaceContainer {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                profile.avatarUri?.let { uri ->
                    // TODO: Load actual image with Coil
                }
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(profile.displayName.ifBlank { "Habit Staker" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(profile.email.ifBlank { "No email set" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

            // Member since
            Text("Member since ${formatDate(profile.joinedAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            // Edit name button
            Button(onClick = { /* TODO: Edit name dialog */ }) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun StatsGrid(profile: UserProfile) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Text("Lifetime Stats", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Staked", formatCurrency(profile.totalStaked), Icons.Default.AttachMoney, Color(0xFF10B981))
            StatCard("Won", formatCurrency(profile.totalWon), Icons.Default.TrendingUp, Color(0xFF3B82F6))
            StatCard("Forfeited", formatCurrency(profile.totalForfeited), Icons.Default.Wallet, Color(0xFFEF4444))
        }
    }
}

@Composable
fun SettingsSection(viewModel: ProfileViewModel, profile: UserProfile) {
    SurfaceContainer {
        Column {
            Text("Preferences", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth())

            SettingRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Use dark theme",
                trailing = {
                    androidx.compose.material3.Switch(
                        checked = profile.darkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            )

            SettingRow(
                icon = Icons.Default.Fingerprint,
                title = "Biometric Lock",
                subtitle = "Require fingerprint/face to open app",
                trailing = {
                    androidx.compose.material3.Switch(
                        checked = profile.biometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometric(it) }
                    )
                }
            )

            SettingRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Reminders and updates",
                trailing = {
                    androidx.compose.material3.Switch(
                        checked = profile.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                }
            )
        }
    }
}

@Composable
fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}

@Composable
fun DangerZone(viewModel: ProfileViewModel, uiState: ProfileUiState) {
    var showResetDialog by remember { mutableStateOf(false) }

    SurfaceContainer {
        Column {
            Text("Danger Zone", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth())

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Reset All Stats", style = MaterialTheme.typography.bodyLarge)
                    Text("Permanently delete all habit data, stakes, and history", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { showResetDialog = true },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("⚠️ Reset All Data?") },
            text = {
                Text("This action cannot be undone. All your habits, completions, stakes, and history will be permanently deleted.")
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmResetStats(); showResetDialog = false }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Yes, Delete Everything")
                }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.statsReset) {
        androidx.compose.material3.Snackbar(
            modifier = Modifier.fillMaxWidth(),
            action = { Text("Dismiss") },
            dismissAction = {}
        ) {
            Text("All stats have been reset")
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatCurrency(cents: Double): String = "\$${(cents / 100).toInt()}"
private fun formatDate(instant: kotlinx.datetime.Instant): String = instant.toString().substring(0, 10)
