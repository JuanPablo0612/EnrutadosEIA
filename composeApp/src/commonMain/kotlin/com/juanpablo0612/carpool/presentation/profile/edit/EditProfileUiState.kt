package com.juanpablo0612.carpool.presentation.profile.edit

data class EditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val bio: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val bioError: String? = null,
    val error: String? = null
)
