package com.habitstakes.app.di

import android.content.Context
import androidx.room.Room
import com.habitstakes.app.data.db.HabitStakesDatabase
import com.habitstakes.app.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitStakesDatabase {
        return Room.databaseBuilder(
            context,
            HabitStakesDatabase::class.java,
            "habit_stakes.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(database: HabitStakesDatabase): HabitRepository =
        HabitRepository(database)

    @Provides
    @Singleton
    fun provideCompletionRepository(database: HabitStakesDatabase): CompletionRepository =
        CompletionRepository(database)

    @Provides
    @Singleton
    fun provideStakeRepository(database: HabitStakesDatabase): StakeRepository =
        StakeRepository(database)

    @Provides
    @Singleton
    fun provideUserProfileRepository(database: HabitStakesDatabase): UserProfileRepository =
        UserProfileRepository(database)

    @Provides
    @Singleton
    fun provideFightChallengeRepository(database: HabitStakesDatabase): FightChallengeRepository =
        FightChallengeRepository(database)
}
