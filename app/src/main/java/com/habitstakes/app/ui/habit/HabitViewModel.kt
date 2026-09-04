package com.habitstakes.app.ui.habit

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.habitstakes.app.data.model.Completion
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.ProofType
import com.habitstakes.app.data.model.Stake
import com.habitstakes.app.data.model.StakeStatus
import com.habitstakes.app.data.model.StakeType
import com.habitstakes.app.data.repository.CompletionRepository
import com.habitstakes.app.data.repository.HabitRepository
import com.habitstakes.app.data.repository.StakeRepository
import com.habitstakes.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository,
    private val stakeRepository: StakeRepository,
    private val profileRepository: UserProfileRepository
) : androidx.lifecycle.ViewModel() {

    private val _habit = MutableStateFlow<Habit?>(null)
    val habit = _habit.asStateFlow()

    private val _completions = MutableStateFlow<List<Completion>>(emptyList())
    val completions = _completions.asStateFlow()

    private val _stakes = MutableStateFlow<List<Stake>>(emptyList())
    val stakes = _stakes.asStateFlow()

    private val _uiState = MutableStateFlow<HabitDetailUiState>(HabitDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var currentHabitId: Long = -1

    fun loadHabit(habitId: Long) {
        currentHabitId = habitId
        viewModelScope.launch {
            val habit = habitRepository.getHabit(habitId)
            _habit.value = habit
            if (habit != null) {
                loadCompletions(habitId)
                loadStakes(habitId)
            }
        }
    }

    private fun loadCompletions(habitId: Long) {
        completionRepository.getCompletionsForHabit(habitId)
            .onEach { _completions.value = it }
            .launchIn(viewModelScope)
    }

    private fun loadStakes(habitId: Long) {
        stakeRepository.getStakesForHabit(habitId)
            .onEach { _stakes.value = it }
            .launchIn(viewModelScope)
    }

    fun submitCompletion(
        habit: Habit,
        proofUri: String?,
        proofType: ProofType,
        textNote: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val completion = Completion(
                    habitId = habit.id,
                    proofUri = proofUri,
                    proofType = proofType,
                    textNote = textNote,
                    verified = habit.autoVerify,
                    verifiedAt = if (habit.autoVerify) Clock.System.now() else null,
                    verifiedBy = if (habit.autoVerify) com.habitstakes.app.data.model.VerificationMethod.AUTO else null
                )
                val completionId = completionRepository.submitCompletion(completion)

                // Create stake record
                val stake = Stake(
                    habitId = habit.id,
                    amount = habit.stakeAmount,
                    currency = habit.currency,
                    status = if (habit.autoVerify) StakeStatus.WON else StakeStatus.HELD
                )
                stakeRepository.createStake(stake)

                // Update habit stats
                habitRepository.recordCompletion(habit.id, habit.stakeAmount)
                profileRepository.addToTotalStaked(habit.stakeAmount)

                if (habit.autoVerify) {
                    stakeRepository.resolveStake(completionId, StakeStatus.WON)
                    profileRepository.addToTotalWon(habit.stakeAmount)
                }

                _uiState.update { it.copy(
                    isSubmitting = false,
                    lastSubmission = SubmissionResult.Success("Submitted successfully!")
                )}
                loadCompletions(habit.id)
                loadStakes(habit.id)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSubmitting = false,
                    lastSubmission = SubmissionResult.Error(e.message ?: "Submission failed")
                )}
            }
        }
    }

    fun markForfeit(habit: Habit, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val completion = Completion(
                    habitId = habit.id,
                    isForfeit = true,
                    forfeitReason = reason,
                    verified = true,
                    verifiedAt = Clock.System.now(),
                    verifiedBy = com.habitstakes.app.data.model.VerificationMethod.AUTO
                )
                completionRepository.submitCompletion(completion)

                val stake = Stake(
                    habitId = habit.id,
                    amount = habit.stakeAmount,
                    currency = habit.currency,
                    status = StakeStatus.FORFEITED,
                    destination = habit.stakeType.defaultDestination()
                )
                stakeRepository.createStake(stake)

                habitRepository.recordForfeit(habit.id, habit.stakeAmount)
                profileRepository.addToTotalForfeited(habit.stakeAmount)

                _uiState.update { it.copy(
                    isSubmitting = false,
                    lastSubmission = SubmissionResult.Success("Forfeit recorded")
                )}
                loadCompletions(habit.id)
                loadStakes(habit.id)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSubmitting = false,
                    lastSubmission = SubmissionResult.Error(e.message ?: "Failed")
                )}
            }
        }
    }
}

data class HabitDetailUiState(
    val isSubmitting: Boolean = false,
    val lastSubmission: SubmissionResult? = null
)

sealed interface SubmissionResult {
    data class Success(val message: String) : SubmissionResult
    data class Error(val message: String) : SubmissionResult
}
