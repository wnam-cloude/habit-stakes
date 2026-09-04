package com.habitstakes.app.data.db.dao

import androidx.room.*
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.HabitWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<Habit>): List<Long>

    @Update
    suspend fun update(habit: Habit): Int

    @Delete
    suspend fun delete(habit: Habit): Int

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): Habit?

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY reminderTime ASC")
    fun getActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isActive = 0 ORDER BY updatedAt DESC")
    fun getArchivedHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isActive = 1 AND time(reminderTime) <= time(:currentTime) ORDER BY reminderTime ASC")
    suspend fun getHabitsDueNow(currentTime: String): List<Habit>

    @Query("SELECT * FROM habits WHERE isActive = 1 AND date(:date) >= date(startDate) AND (endDate IS NULL OR date(:date) <= date(endDate))")
    suspend fun getHabitsForDate(date: String): List<Habit>

    @Query("SELECT COUNT(*) FROM habits WHERE isActive = 1")
    suspend fun getActiveCount(): Int

    @Query("SELECT COUNT(*) FROM habits WHERE isActive = 1 AND date(:date) >= date(startDate) AND (endDate IS NULL OR date(:date) <= date(endDate))")
    suspend fun getActiveCountForDate(date: String): Int

    // Custom query for HabitWithStats
    @Query("""
        SELECT h.*, 
            COALESCE(c.verified_count, 0) as verifiedCount,
            COALESCE(c.total_count, 0) as totalCount,
            COALESCE(c.last_completed_at, null) as lastCompletedAt,
            COALESCE(c.current_streak, 0) as currentStreak,
            COALESCE(c.longest_streak, 0) as longestStreak
        FROM habits h
        LEFT JOIN (
            SELECT habitId,
                SUM(CASE WHEN verified = 1 THEN 1 ELSE 0 END) as verified_count,
                COUNT(*) as total_count,
                MAX(submittedAt) as last_completed_at,
                0 as current_streak,
                0 as longest_streak
            FROM completions
            GROUP BY habitId
        ) c ON h.id = c.habitId
        WHERE h.isActive = 1
        ORDER BY h.reminderTime ASC
    """)
    fun getActiveHabitsWithStats(): Flow<List<HabitWithStats>>

    @Query("UPDATE habits SET lastCompletedAt = :now, currentStreak = currentStreak + 1, totalCompletions = totalCompletions + 1, totalStaked = totalStaked + :stakeAmount WHERE id = :habitId")
    suspend fun recordCompletion(habitId: Long, now: Instant, stakeAmount: Double): Int

    @Query("UPDATE habits SET totalForfeits = totalForfeits + 1, totalForfeited = totalForfeited + :amount WHERE id = :habitId")
    suspend fun recordForfeit(habitId: Long, amount: Double): Int

    @Query("UPDATE habits SET isActive = 0 WHERE id = :id")
    suspend fun archiveHabit(id: Long): Int

    @Query("UPDATE habits SET isActive = 1 WHERE id = :id")
    suspend fun unarchiveHabit(id: Long): Int
}
