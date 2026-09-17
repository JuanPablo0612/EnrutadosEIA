package com.juanpablo0612.carpool.presentation.profile.passenger

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile

data class PassengerProfileUiState(
    val isLoading: Boolean = true,
    val profile: PublicProfile? = null
)
