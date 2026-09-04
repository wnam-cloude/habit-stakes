package com.habitstakes.app.data.repository

import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.db.dao.HabitDao
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.HabitWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val database: HabitStakesDatabase
) {
    private val dao: HabitDao = database.habitDao()

    suspend fun createHabit(habit: Habit): Long = dao.insert(habit)

    suspend fun updateHabit(habit: Habit) = dao.update(habit)

    suspend fun deleteHabit(id: Long) = dao.deleteById(id)

    suspend fun getHabit(id: Long): Habit? = dao.getById(id)

    fun getActiveHabits(): Flow<List<Habit>> = dao.getActiveHabits()

    fun getArchivedHabits(): Flow<List<Habit>> = dao.getArchivedHabits()

    fun getAllHabits(): Flow<List<Habit>> = dao.getAllHabits()

    fun getActiveHabitsWithStats(): Flow<List<HabitWithStats>> = dao.getActiveHabitsWithStats()

    fun getHabitsDueNow(currentTime: String): Flow<List<Habit>> = dao.getHabitsDueNow(currentTime)

    suspend fun recordCompletion(habitId: Long, stakeAmount: Double) {
        val now = Instant.now()
        dao.recordCompletion(habitId, now, stakeAmount)
    }

    suspend fun recordForfeit(habitId: Long, amount: Double) = dao.recordForfeit(habitId, amount)

    suspend fun getActiveCount(): Int = dao.getActiveCount()
}
