package com.habitstakes.app.data.repository

import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.db.dao.FightChallengeDao
import com.habitstakes.app.data.model.FightChallenge
import com.habitstakes.app.data.model.FightStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FightChallengeRepository @Inject constructor(
    private val database: HabitStakesDatabase
) {
    private val dao: FightChallengeDao = database.fightChallengeDao()

    suspend fun createChallenge(challenge: FightChallenge): Long = dao.insert(challenge)

    suspend fun updateChallenge(challenge: FightChallenge) = dao.update(challenge)

    suspend fun getChallenge(id: Long): FightChallenge? = dao.getById(id)

    fun getUpcomingChallenges(): Flow<List<FightChallenge>> = dao.getByStatus(FightStatus.UPCOMING)

    fun getActiveChallenges(): Flow<List<FightChallenge>> = dao.getByStatus(FightStatus.ACTIVE)

    fun getAllActiveAndUpcoming(): Flow<List<FightChallenge>> = dao.getByStatuses(
        listOf(FightStatus.UPCOMING, FightStatus.ACTIVE)
    )

    fun getChallengesForHabit(habitId: Long): Flow<List<FightChallenge>> = dao.getByHabitId(habitId)

    suspend fun joinChallenge(challengeId: Long, entryFee: Double) = dao.joinChallenge(challengeId, entryFee)

    suspend fun updateStatus(id: Long, status: FightStatus) = dao.updateStatus(id, status)
}
