package com.juanpablo0612.carpool.presentation.profile.edit

import com.juanpablo0612.carpool.domain.auth.model.AuthError

data class EditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val bio: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: EditProfileFieldError? = null,
    val bioError: EditProfileFieldError? = null,
    val error: AuthError? = null
)
