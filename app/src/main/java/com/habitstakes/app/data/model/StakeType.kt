package com.habitstakes.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.habitstakes.app.data.db.converters.DateTimeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * Type of stake - how the user puts skin in the game
 */
enum class StakeType(val displayName: String, val description: String) {
    MONEY("Money Stake", "Pledge real money - forfeited if you fail"),
    SOCIAL("Social Stake", "Public commitment - reputation on the line"),
    TIME("Time Stake", "Lock time - you can't access apps until done"),
    COMBO("Combo Stake", "Money + Social + Time combined");

    fun defaultDestination(): StakeDestination = when (this) {
        MONEY -> StakeDestination.PRIZE_POOL
        SOCIAL -> StakeDestination.CHARITY
        TIME -> StakeDestination.BURN
        COMBO -> StakeDestination.PRIZE_POOL
    }
}

@Serializable
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val stakeType: StakeType = StakeType.MONEY,
    val stakeAmount: Double = 0.0, // USD cents for money, minutes for time
    val currency: String = "USD",
    val frequency: Frequency = Frequency.DAILY,
    val reminderTime: String = "07:00", // HH:mm format
    val proofType: ProofType = ProofType.PHOTO,
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val startDate: Instant = Clock.System.now(),
    val endDate: Instant? = null,
    val gracePeriodMinutes: Int = 30,
    val autoVerify: Boolean = false,
    val tags: String = "", // comma-separated
    @TypeConverters(DateTimeConverter::class)
    val lastCompletedAt: Instant? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val totalForfeits: Int = 0,
    val totalStaked: Double = 0.0,
    val totalForfeited: Double = 0.0
)

@Serializable
enum class Frequency(val displayName: String, val cronExpression: String) {
    DAILY("Daily", "0 0 * * *"),
    WEEKDAYS("Weekdays", "0 0 * * 1-5"),
    WEEKENDS("Weekends", "0 0 * * 0,6"),
    CUSTOM("Custom", ""), // User defines days
    INTERVAL("Every N Days", "") // e.g., every 2 days
}

@Serializable
enum class ProofType(val displayName: String, val requiresCamera: Boolean, val icon: String) {
    PHOTO("Photo", true, "camera"),
    VIDEO("Video (15s)", true, "videocam"),
    TIMELAPSE("Timelapse (30s)", true, "timelapse"),
    SCREENSHOT("Screenshot", false, "screenshot"),
    TEXT("Text Note", false, "edit"),
    GPS("Location Check-in", false, "location_on"),
    BIOMETRIC("Biometric", false, "fingerprint")
}

@Serializable
@Entity(tableName = "completions")
data class Completion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val proofUri: String? = null,
    val proofType: ProofType = ProofType.PHOTO,
    val textNote: String? = null,
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    val verified: Boolean = false,
    val verificationScore: Double = 0.0, // 0.0 - 1.0 AI confidence
    val verifiedAt: Instant? = null,
    val verifiedBy: VerificationMethod = VerificationMethod.AUTO,
    val submittedAt: Instant = Clock.System.now(),
    val isForfeit: Boolean = false,
    val forfeitReason: String? = null
)

@Serializable
enum class VerificationMethod(val displayName: String) {
    AUTO("AI Auto-Verify"),
    MANUAL("Human Review"),
    PEER("Community Vote"),
    BIOMETRIC("Biometric Match")
}

@Serializable
@Entity(tableName = "stakes")
data class Stake(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val amount: Double, // in cents for money
    val currency: String = "USD",
    val status: StakeStatus = StakeStatus.PENDING,
    val createdAt: Instant = Clock.System.now(),
    val resolvedAt: Instant? = null,
    val transactionId: String? = null,
    val destination: StakeDestination = StakeDestination.PRIZE_POOL
)

@Serializable
enum class StakeStatus(val displayName: String, val color: String) {
    PENDING("Pending", "#F59E0B"),
    HELD("Held", "#3B82F6"),
    WON("Completed", "#10B981"),
    FORFEITED("Forfeited", "#EF4444"),
    REFUNDED("Refunded", "#6366F1"),
    DISPUTED("Disputed", "#F97316")
}

@Serializable
enum class StakeDestination(val displayName: String, val description: String) {
    PRIZE_POOL("Prize Pool", "Funds Fight Mode winners"),
    CHARITY("Charity", "Donated to selected cause"),
    FRIEND("Friend", "Sent to accountability partner"),
    BURN("Burned", "Permanently removed from circulation"),
    REFUND("Refunded", "Returned to your balance")
}

@Serializable
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1, // Singleton
    val displayName: String = "",
    val email: String = "",
    val avatarUri: String? = null,
    val totalStaked: Double = 0.0,
    val totalWon: Double = 0.0,
    val totalForfeited: Double = 0.0,
    val currentBalance: Double = 0.0, // Internal balance in cents
    val preferredCurrency: String = "USD",
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val weeklyReportDay: Int = 7, // Sunday = 7
    val timezone: String = "UTC",
    val joinedAt: Instant = Clock.System.now(),
    val lastActiveAt: Instant = Clock.System.now()
)

@Serializable
@Entity(tableName = "fight_challenges")
data class FightChallenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val habitId: Long,
    val entryFee: Double, // in cents
    val maxParticipants: Int = 10,
    val currentParticipants: Int = 0,
    val startDate: Instant,
    val endDate: Instant,
    val status: FightStatus = FightStatus.UPCOMING,
    val prizePool: Double = 0.0,
    val winnerIds: String = "", // comma-separated user IDs
    val createdBy: String = "system"
)

@Serializable
enum class FightStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    ACTIVE("Active"),
    ENDED("Ended"),
    CANCELLED("Cancelled")
}

@Serializable
data class HabitWithStats(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val stakeType: StakeType = StakeType.MONEY,
    val stakeAmount: Double = 0.0,
    val currency: String = "USD",
    val frequency: Frequency = Frequency.DAILY,
    val reminderTime: String = "07:00",
    val proofType: ProofType = ProofType.PHOTO,
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val startDate: Instant = Clock.System.now(),
    val endDate: Instant? = null,
    val gracePeriodMinutes: Int = 30,
    val autoVerify: Boolean = false,
    val tags: String = "",
    val lastCompletedAt: Instant? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val totalForfeits: Int = 0,
    val totalStaked: Double = 0.0,
    val totalForfeited: Double = 0.0,
    // Computed stats from joins
    val verifiedCount: Int = 0,
    val totalCount: Int = 0
)

