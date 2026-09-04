package com.habitstakes.app.data.db.dao

import androidx.room.*
import com.habitstakes.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile): Long

    @Update
    suspend fun update(profile: UserProfile): Int

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfile?

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfile?>

    @Query("UPDATE user_profile SET totalStaked = totalStaked + :amount WHERE id = 1")
    suspend fun addToTotalStaked(amount: Double): Int

    @Query("UPDATE user_profile SET totalWon = totalWon + :amount WHERE id = 1")
    suspend fun addToTotalWon(amount: Double): Int

    @Query("UPDATE user_profile SET totalForfeited = totalForfeited + :amount WHERE id = 1")
    suspend fun addToTotalForfeited(amount: Double): Int

    @Query("UPDATE user_profile SET currentBalance = currentBalance + :amount WHERE id = 1")
    suspend fun adjustBalance(amount: Double): Int

    @Query("UPDATE user_profile SET lastActiveAt = :now WHERE id = 1")
    suspend fun updateLastActive(now: kotlinx.datetime.Instant): Int
}
