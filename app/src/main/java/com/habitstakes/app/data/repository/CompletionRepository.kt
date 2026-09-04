package com.habitstakes.app.data.repository

import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.db.dao.CompletionDao
import com.habitstakes.app.data.model.Completion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletionRepository @Inject constructor(
    private val database: HabitStakesDatabase
) {
    private val dao: CompletionDao = database.completionDao()

    suspend fun submitCompletion(completion: Completion): Long = dao.insert(completion)

    suspend fun updateCompletion(completion: Completion) = dao.update(completion)

    fun getCompletionsForHabit(habitId: Long): Flow<List<Completion>> = dao.getByHabitId(habitId)

    suspend fun getCompletionsForHabitInRange(
        habitId: Long, start: Instant, end: Instant
    ): List<Completion> = dao.getByHabitIdAndDateRange(habitId, start, end)

    fun getPendingVerifications(): Flow<List<Completion>> = dao.getPendingVerifications()

    fun getForfeits(): Flow<List<Completion>> = dao.getForfeits()

    suspend fun getTodaysCompletion(habitId: Long, date: Instant): Completion? = 
        dao.getTodaysCompletion(habitId, date)

    suspend fun getVerifiedCount(habitId: Long): Int = dao.getVerifiedCount(habitId)

    suspend fun getTotalCount(habitId: Long): Int = dao.getTotalCount(habitId)
}
