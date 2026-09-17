package com.juanpablo0612.carpool.presentation.profile.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassengerProfileViewModel(
    userId: String,
    authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PassengerProfileUiState())
    val state: StateFlow<PassengerProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = authRepository.getPublicProfile(userId).getOrNull()
            _state.update { it.copy(isLoading = false, profile = profile) }
        }
    }
}
