package com.habitstakes.app.data.repository

import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.db.dao.UserProfileDao
import com.habitstakes.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val database: HabitStakesDatabase
) {
    private val dao: UserProfileDao = database.userProfileDao()

    suspend fun createProfile(profile: UserProfile) = dao.insert(profile)

    suspend fun updateProfile(profile: UserProfile) = dao.update(profile)

    suspend fun getProfile(): UserProfile? = dao.getProfile()

    fun observeProfile(): Flow<UserProfile?> = dao.observeProfile()

    suspend fun addToTotalStaked(amount: Double) = dao.addToTotalStaked(amount)

    suspend fun addToTotalWon(amount: Double) = dao.addToTotalWon(amount)

    suspend fun addToTotalForfeited(amount: Double) = dao.addToTotalForfeited(amount)

    suspend fun adjustBalance(amount: Double) = dao.adjustBalance(amount)

    suspend fun updateLastActive(now: Instant) = dao.updateLastActive(now)

    suspend fun getOrCreateProfile(): UserProfile {
        return getProfile() ?: UserProfile().also { createProfile(it) }
    }
}
