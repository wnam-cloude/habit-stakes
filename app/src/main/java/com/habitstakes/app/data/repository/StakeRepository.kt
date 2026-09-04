package com.habitstakes.app.data.repository

import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.db.dao.StakeDao
import com.habitstakes.app.data.model.Stake
import com.habitstakes.app.data.model.StakeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StakeRepository @Inject constructor(
    private val database: HabitStakesDatabase
) {
    private val dao: StakeDao = database.stakeDao()

    suspend fun createStake(stake: Stake): Long = dao.insert(stake)

    suspend fun updateStake(stake: Stake) = dao.update(stake)

    suspend fun getStake(id: Long): Stake? = dao.getById(id)

    fun getStakesForHabit(habitId: Long): Flow<List<Stake>> = dao.getByHabitId(habitId)

    fun getStakesByStatus(status: StakeStatus): Flow<List<Stake>> = dao.getByStatus(status)

    fun getStakesByStatuses(statuses: List<StakeStatus>): Flow<List<Stake>> = dao.getByStatuses(statuses)

    suspend fun getTotalByStatus(status: StakeStatus): Double = dao.getTotalByStatus(status) ?: 0.0

    suspend fun getTotalByHabitAndStatus(habitId: Long, status: StakeStatus): Double = 
        dao.getTotalByHabitAndStatus(habitId, status) ?: 0.0

    suspend fun resolveStake(id: Long, newStatus: StakeStatus) = 
        dao.updateStatus(id, newStatus, Instant.now())

    suspend fun getRecentStakes(limit: Int = 50): List<Stake> = dao.getRecent(limit)
}
