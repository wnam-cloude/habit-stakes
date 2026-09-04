package com.habitstakes.app.ui.home

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.HabitWithStats
import com.habitstakes.app.data.repository.HabitRepository
import com.habitstakes.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val profileRepository: UserProfileRepository
) : androidx.lifecycle.ViewModel() {

    private val _habits = MutableStateFlow<List<HabitWithStats>>(emptyList())
    val habits = _habits

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile

    // Combined UI state
    val uiState = combine(
        habits,
        profile
    ) { habits, profile ->
        HomeUiState(
            habits = habits,
            profile = profile,
            currentTime = Clock.System.now()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState.empty()
    )

    init {
        loadData()
    }

    private fun loadData() {
        habitRepository.getActiveHabitsWithStats()
            .onEach { _habits.value = it }
            .launchIn(viewModelScope)

        profileRepository.observeProfile()
            .onEach { _profile.value = it }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        loadData()
    }

    fun getTodaysDueHabits(): List<HabitWithStats> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentTime = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
        return _habits.value.filter { habit ->
            habit.habit.isActive && habit.habit.reminderTime <= currentTime
        }
    }
}

data class HomeUiState(
    val habits: List<HabitWithStats> = emptyList(),
    val profile: UserProfile? = null,
    val currentTime: Instant = Clock.System.now(),
    val isLoading: Boolean = false
) {
    companion object {
        fun empty() = HomeUiState()
    }

    val activeHabits: List<HabitWithStats>
        get() = habits.filter { it.habit.isActive }

    val totalStaked: Double
        get() = profile?.totalStaked ?: 0.0

    val totalWon: Double
        get() = profile?.totalWon ?: 0.0

    val totalForfeited: Double
        get() = profile?.totalForfeited ?: 0.0

    val currentStreak: Int
        get() = habits.maxOfOrNull { it.habit.currentStreak } ?: 0
}
