package com.habitstakes.app.ui.create

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitstakes.app.data.model.Frequency
import com.habitstakes.app.data.model.Habit
import com.habitstakes.app.data.model.ProofType
import com.habitstakes.app.data.model.StakeType
import com.habitstakes.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    // Form state
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _stakeType = MutableStateFlow(StakeType.MONEY)
    val stakeType = _stakeType.asStateFlow()

    private val _stakeAmount = MutableStateFlow("")
    val stakeAmount = _stakeAmount.asStateFlow()

    private val _frequency = MutableStateFlow(Frequency.DAILY)
    val frequency = _frequency.asStateFlow()

    private val _reminderTime = MutableStateFlow("07:00")
    val reminderTime = _reminderTime.asStateFlow()

    private val _proofType = MutableStateFlow(ProofType.PHOTO)
    val proofType = _proofType.asStateFlow()

    private val _gracePeriod = MutableStateFlow(30)
    val gracePeriod = _gracePeriod.asStateFlow()

    private val _autoVerify = MutableStateFlow(false)
    val autoVerify = _autoVerify.asStateFlow()

    private val _tags = MutableStateFlow("")
    val tags = _tags.asStateFlow()

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(value: String) { _title.value = value; validate() }
    fun updateDescription(value: String) { _description.value = value }
    fun updateStakeType(value: StakeType) { _stakeType.value = value; validate() }
    fun updateStakeAmount(value: String) { _stakeAmount.value = value; validate() }
    fun updateFrequency(value: Frequency) { _frequency.value = value }
    fun updateReminderTime(value: String) { _reminderTime.value = value }
    fun updateProofType(value: ProofType) { _proofType.value = value }
    fun updateGracePeriod(value: Int) { _gracePeriod.value = value }
    fun updateAutoVerify(value: Boolean) { _autoVerify.value = value }
    fun updateTags(value: String) { _tags.value = value }

    private fun validate() {
        val isValid = _title.value.isNotBlank() &&
            (_stakeType.value != StakeType.MONEY || _stakeAmount.value.toDoubleOrNull() != null && _stakeAmount.value.toDouble() > 0)
        _uiState.update { it.copy(isValid = isValid) }
    }

    fun createHabit(): Habit? {
        val amount = _stakeAmount.value.toDoubleOrNull() ?: 0.0
        val stakeAmountCents = if (_stakeType.value == StakeType.MONEY) (amount * 100).toLong() else 0L
        val habit = Habit(
            title = _title.value.trim(),
            description = _description.value.trim(),
            stakeType = _stakeType.value,
            stakeAmount = stakeAmountCents,
            frequency = _frequency.value,
            reminderTime = _reminderTime.value,
            proofType = _proofType.value,
            gracePeriodMinutes = _gracePeriod.value,
            autoVerify = _autoVerify.value,
            tags = _tags.value.trim(),
            startDate = Clock.System.now(),
            endDate = null
        )
        viewModelScope.launch {
            val id = habitRepository.createHabit(habit)
            _uiState.update { it.copy(createdHabitId = id) }
        }
        return habit
    }

    fun resetForm() {
        _title.value = ""
        _description.value = ""
        _stakeType.value = StakeType.MONEY
        _stakeAmount.value = ""
        _frequency.value = Frequency.DAILY
        _reminderTime.value = "07:00"
        _proofType.value = ProofType.PHOTO
        _gracePeriod.value = 30
        _autoVerify.value = false
        _tags.value = ""
        _uiState.value = CreateHabitUiState()
    }
}

data class CreateHabitUiState(
    val isValid: Boolean = false,
    val createdHabitId: Long? = null,
    val error: String? = null
)
