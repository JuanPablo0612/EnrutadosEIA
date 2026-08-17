package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole

data class ProfileUiState(
    val user: User? = null,
    val activeRole: UserRole? = null,
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val showActiveRolesDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val deleteAccountNameInput: String = "",
    val blockedRoleToggle: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteAccountError: AuthError? = null
)
