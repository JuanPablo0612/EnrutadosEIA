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
    private val userId: String,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PassengerProfileUiState())
    val state: StateFlow<PassengerProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun retry() = loadProfile()

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true, error = false) }
        viewModelScope.launch {
            authRepository.getPublicProfile(userId)
                .onSuccess { profile ->
                    _state.update { it.copy(isLoading = false, profile = profile, error = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, profile = null, error = true) }
                }
        }
    }
}
