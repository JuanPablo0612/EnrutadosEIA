package com.juanpablo0612.carpool.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnNextPage -> {
                val current = _state.value.currentPage
                if (current < _state.value.totalPages - 1) {
                    _state.update { it.copy(currentPage = current + 1) }
                }
            }
            OnboardingAction.OnSkip, OnboardingAction.OnFinish -> finish()
        }
    }

    private fun finish() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingSeen()
            _events.emit(OnboardingEvent.NavigateToApp)
        }
    }
}
