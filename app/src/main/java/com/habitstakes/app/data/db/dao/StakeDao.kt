package com.habitstakes.app.data.db.dao

import androidx.room.*
import com.habitstakes.app.data.model.Stake
import com.habitstakes.app.data.model.StakeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface StakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stake: Stake): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stakes: List<Stake>): List<Long>

    @Update
    suspend fun update(stake: Stake): Int

    @Query("SELECT * FROM stakes WHERE id = :id")
    suspend fun getById(id: Long): Stake?

    @Query("SELECT * FROM stakes WHERE habitId = :habitId ORDER BY createdAt DESC")
    fun getByHabitId(habitId: Long): Flow<List<Stake>>

    @Query("SELECT * FROM stakes WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: StakeStatus): Flow<List<Stake>>

    @Query("SELECT * FROM stakes WHERE status IN (:statuses) ORDER BY createdAt DESC")
    fun getByStatuses(statuses: List<StakeStatus>): Flow<List<Stake>>

    @Query("SELECT SUM(amount) FROM stakes WHERE status = :status")
    suspend fun getTotalByStatus(status: StakeStatus): Double?

    @Query("SELECT SUM(amount) FROM stakes WHERE habitId = :habitId AND status = :status")
    suspend fun getTotalByHabitAndStatus(habitId: Long, status: StakeStatus): Double?

    @Query("UPDATE stakes SET status = :newStatus, resolvedAt = :now WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: StakeStatus, now: Instant): Int

    @Query("SELECT * FROM stakes ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<Stake>
}
