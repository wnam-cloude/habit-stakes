package com.habitstakes.app.data.db.dao

import androidx.room.*
import com.habitstakes.app.data.model.Completion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface CompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: Completion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(completions: List<Completion>): List<Long>

    @Update
    suspend fun update(completion: Completion): Int

    @Delete
    suspend fun delete(completion: Completion): Int

    @Query("SELECT * FROM completions WHERE id = :id")
    suspend fun getById(id: Long): Completion?

    @Query("SELECT * FROM completions WHERE habitId = :habitId ORDER BY submittedAt DESC")
    fun getByHabitId(habitId: Long): Flow<List<Completion>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId AND submittedAt >= :start AND submittedAt <= :end ORDER BY submittedAt DESC")
    suspend fun getByHabitIdAndDateRange(habitId: Long, start: Instant, end: Instant): List<Completion>

    @Query("SELECT * FROM completions WHERE verified = 0 ORDER BY submittedAt ASC")
    fun getPendingVerifications(): Flow<List<Completion>>

    @Query("SELECT * FROM completions WHERE isForfeit = 1 ORDER BY submittedAt DESC")
    fun getForfeits(): Flow<List<Completion>>

    @Query("SELECT COUNT(*) FROM completions WHERE habitId = :habitId AND verified = 1")
    suspend fun getVerifiedCount(habitId: Long): Int

    @Query("SELECT COUNT(*) FROM completions WHERE habitId = :habitId")
    suspend fun getTotalCount(habitId: Long): Int

    @Query("SELECT * FROM completions WHERE habitId = :habitId AND date(submittedAt) = date(:date) LIMIT 1")
    suspend fun getTodaysCompletion(habitId: Long, date: Instant): Completion?

    @Query("DELETE FROM completions WHERE habitId = :habitId AND submittedAt < :cutoff")
    suspend fun deleteOldCompletions(habitId: Long, cutoff: Instant): Int
}
