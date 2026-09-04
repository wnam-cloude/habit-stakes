package com.habitstakes.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habitstakes.app.data.db.converters.DateTimeConverter
import com.habitstakes.app.data.db.dao.*
import com.habitstakes.app.data.model.*

@TypeConverters(DateTimeConverter::class)
@Database(
    entities = [
        Habit::class,
        Completion::class,
        Stake::class,
        UserProfile::class,
        FightChallenge::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HabitStakesDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
    abstract fun stakeDao(): StakeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun fightChallengeDao(): FightChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: HabitStakesDatabase? = null

        fun getInstance(context: Context): HabitStakesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitStakesDatabase::class.java,
                    "habit_stakes.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
