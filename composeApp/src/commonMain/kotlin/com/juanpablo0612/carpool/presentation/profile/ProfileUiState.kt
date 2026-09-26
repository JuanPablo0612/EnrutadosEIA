package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val deleteAccountNameInput: String = "",
    val isDeleting: Boolean = false,
    val deleteAccountError: AuthError? = null
)
