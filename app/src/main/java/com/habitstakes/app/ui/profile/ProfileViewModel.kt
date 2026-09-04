package com.habitstakes.app.ui.profile

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.habitstakes.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository
) : androidx.lifecycle.ViewModel() {

    private val _profile = MutableStateFlow<com.habitstakes.app.data.model.UserProfile?>(null)
    val profile = _profile.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        profileRepository.observeProfile()
            .onEach { _profile.value = it }
            .launchIn(viewModelScope)
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(displayName = name)
            profileRepository.updateProfile(updated)
            _uiState.update { it.copy(saved = true) }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(darkMode = enabled)
            profileRepository.updateProfile(updated)
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(biometricEnabled = enabled)
            profileRepository.updateProfile(updated)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(notificationsEnabled = enabled)
            profileRepository.updateProfile(updated)
        }
    }

    fun resetStats() {
        _uiState.update { it.copy(showResetConfirm = true) }
    }

    fun confirmResetStats() {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val reset = current.copy(
                totalStaked = 0.0,
                totalWon = 0.0,
                totalForfeited = 0.0,
                currentBalance = 0.0
            )
            profileRepository.updateProfile(reset)
            _uiState.update { it.copy(showResetConfirm = false, statsReset = true) }
        }
    }
}

data class ProfileUiState(
    val saved: Boolean = false,
    val showResetConfirm: Boolean = false,
    val statsReset: Boolean = false
)
