package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole

data class ProfileUiState(
    val user: User? = null,
    val activeRole: UserRole? = null,
    val isLoading: Boolean = true
)
