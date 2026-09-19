package com.juanpablo0612.carpool.presentation.profile.edit

import com.juanpablo0612.carpool.presentation.auth.AuthError
import io.github.vinceglb.filekit.PlatformFile

data class EditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val bio: String = "",
    val existingPhotoUrl: String? = null,
    val photoFile: PlatformFile? = null,
    val photoError: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: EditProfileFieldError? = null,
    val phoneError: EditProfileFieldError? = null,
    val bioError: EditProfileFieldError? = null,
    val error: AuthError? = null
)
