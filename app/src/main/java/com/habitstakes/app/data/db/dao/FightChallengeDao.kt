package com.habitstakes.app.data.db.dao

import androidx.room.*
import com.habitstakes.app.data.model.FightChallenge
import com.habitstakes.app.data.model.FightStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface FightChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challenge: FightChallenge): Long

    @Update
    suspend fun update(challenge: FightChallenge): Int

    @Query("SELECT * FROM fight_challenges WHERE id = :id")
    suspend fun getById(id: Long): FightChallenge?

    @Query("SELECT * FROM fight_challenges WHERE status = :status ORDER BY startDate ASC")
    fun getByStatus(status: FightStatus): Flow<List<FightChallenge>>

    @Query("SELECT * FROM fight_challenges WHERE status IN (:statuses) ORDER BY startDate ASC")
    fun getByStatuses(statuses: List<FightStatus>): Flow<List<FightChallenge>>

    @Query("SELECT * FROM fight_challenges WHERE habitId = :habitId ORDER BY startDate DESC")
    fun getByHabitId(habitId: Long): Flow<List<FightChallenge>>

    @Query("UPDATE fight_challenges SET currentParticipants = currentParticipants + 1, prizePool = prizePool + :entryFee WHERE id = :id")
    suspend fun joinChallenge(id: Long, entryFee: Double): Int

    @Query("UPDATE fight_challenges SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: FightStatus): Int
}
