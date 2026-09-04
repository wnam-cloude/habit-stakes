package com.habitstakes.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitstakes.app.data.model.StakeStatus
import com.habitstakes.app.data.model.HabitWithStats
import com.habitstakes.app.data.model.StakeType

@Composable
fun StakeStatusChip(
    status: StakeStatus,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val colors = statusColors(status)
    val animatedColor by animateColorAsState(colors.background, label = "status bg")
    val animatedTextColor by animateColorAsState(colors.text, label = "status text")

    Card(
        modifier = modifier
            .padding(4.dp)
            .height(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor,
            contentColor = animatedTextColor
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = statusIcon(status),
                    contentDescription = null,
                    tint = animatedTextColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = status.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habit: HabitWithStats,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val stakeColor = stakeTypeColor(habit.stakeType)
    val successRate = if (habit.totalCount > 0) habit.verifiedCount.toDouble() / habit.totalCount else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                StakeStatusChip(
                    status = if (habit.currentStreak > 0) StakeStatus.HELD else StakeStatus.PENDING,
                    showIcon = false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatPill(
                    label = "Streak",
                    value = "${habit.currentStreak}",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF6B35)
                )
                StatPill(
                    label = "Success",
                    value = "${(successRate * 100).toInt()}%",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF2E7D32)
                )
                StatPill(
                    label = "Stake",
                    value = formatStake(habit),
                    icon = stakeTypeIcon(habit.stakeType),
                    color = stakeColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminder: ${habit.reminderTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = habit.frequency.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp, 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
    }
}

private data class StatusColors(val background: Color, val text: Color)

private fun statusColors(status: StakeStatus): StatusColors = when (status) {
    StakeStatus.PENDING -> StatusColors(Color(0xFFF59E0B), Color(0xFF78350F))
    StakeStatus.HELD -> StatusColors(Color(0xFF3B82F6), Color(0xFFFFFFFF))
    StakeStatus.WON -> StatusColors(Color(0xFF10B981), Color(0xFFFFFFFF))
    StakeStatus.FORFEITED -> StatusColors(Color(0xFFEF4444), Color(0xFFFFFFFF))
    StakeStatus.REFUNDED -> StatusColors(Color(0xFF6366F1), Color(0xFFFFFFFF))
    StakeStatus.DISPUTED -> StatusColors(Color(0xFFF97316), Color(0xFFFFFFFF))
}

private fun statusIcon(status: StakeStatus) = when (status) {
    StakeStatus.PENDING -> Icons.Default.Info
    StakeStatus.HELD -> Icons.Default.Schedule
    StakeStatus.WON -> Icons.Default.CheckCircle
    StakeStatus.FORFEITED -> Icons.Default.Cancel
    StakeStatus.REFUNDED -> Icons.Default.Refresh
    StakeStatus.DISPUTED -> Icons.Default.Warning
}

private fun stakeTypeColor(type: StakeType): Color = when (type) {
    StakeType.MONEY -> Color(0xFF10B981)
    StakeType.SOCIAL -> Color(0xFF3B82F6)
    StakeType.TIME -> Color(0xFF8B5CF6)
    StakeType.COMBO -> Color(0xFFEC4899)
}

private fun stakeTypeIcon(type: StakeType) = when (type) {
    StakeType.MONEY -> Icons.Default.AttachMoney
    StakeType.SOCIAL -> Icons.Default.People
    StakeType.TIME -> Icons.Default.Timer
    StakeType.COMBO -> Icons.Default.AutoAwesome
}

private fun formatStake(habit: HabitWithStats): String = when (habit.stakeType) {
    StakeType.MONEY -> "$${(habit.stakeAmount / 100).toInt()}"
    StakeType.TIME -> "${habit.stakeAmount.toInt()}min"
    else -> habit.stakeType.displayName
}
