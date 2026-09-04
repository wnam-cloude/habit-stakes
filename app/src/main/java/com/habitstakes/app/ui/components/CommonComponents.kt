package com.habitstakes.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloseCircle
import androidx.compose.material.icons.filled.HelpCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitstakes.app.data.model.StakeStatus
import com.habitstakes.app.ui.theme.HabitStakesTheme

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

@Composable
fun HabitCard(
    habit: com.habitstakes.app.data.model.HabitWithStats,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val habitModel = habit.habit
    val stakeColor = stakeTypeColor(habitModel.stakeType)

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
                        text = habitModel.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                    if (habitModel.description.isNotBlank()) {
                        Text(
                            text = habitModel.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                        )
                    }
                }
                StakeStatusChip(
                    status = habitModel.currentStreak > 0 ? StakeStatus.HELD : StakeStatus.PENDING,
                    showIcon = false
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatPill(
                    label = "Streak",
                    value = "${habitModel.currentStreak}",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF6B35)
                )
                StatPill(
                    label = "Success",
                    value = "${(habit.successRate * 100).toInt()}%",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF2E7D32)
                )
                StatPill(
                    label = "Stake",
                    value = formatStake(habitModel),
                    icon = stakeTypeIcon(habitModel.stakeType),
                    color = stakeColor
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // Bottom info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminder: ${habitModel.reminderTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = habitModel.frequency.displayName,
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
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
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
    StakeStatus.PENDING -> Icons.Default.HelpCircle
    StakeStatus.HELD -> Icons.Default.Schedule
    StakeStatus.WON -> Icons.Default.CheckCircle
    StakeStatus.FORFEITED -> Icons.Default.CloseCircle
    StakeStatus.REFUNDED -> Icons.Default.Refresh
    StakeStatus.DISPUTED -> Icons.Default.Warning
}

private fun stakeTypeColor(type: com.habitstakes.app.data.model.StakeType): Color = when (type) {
    com.habitstakes.app.data.model.StakeType.MONEY -> Color(0xFF10B981)
    com.habitstakes.app.data.model.StakeType.SOCIAL -> Color(0xFF3B82F6)
    com.habitstakes.app.data.model.StakeType.TIME -> Color(0xFF8B5CF6)
    com.habitstakes.app.data.model.StakeType.COMBO -> Color(0xFFEC4899)
}

private fun stakeTypeIcon(type: com.habitstakes.app.data.model.StakeType) = when (type) {
    com.habitstakes.app.data.model.StakeType.MONEY -> Icons.Default.AttachMoney
    com.habitstakes.app.data.model.StakeType.SOCIAL -> Icons.Default.People
    com.habitstakes.app.data.model.StakeType.TIME -> Icons.Default.Timer
    com.habitstakes.app.data.model.StakeType.COMBO -> Icons.Default.AutoAwesome
}

private fun formatStake(habit: com.habitstakes.app.data.model.Habit): String = when (habit.stakeType) {
    com.habitstakes.app.data.model.StakeType.MONEY -> "$${(habit.stakeAmount / 100).toInt()}"
    com.habitstakes.app.data.model.StakeType.TIME -> "${habit.stakeAmount.toInt()}min"
    else -> habit.stakeType.displayName
}
