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
    val isSaved: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val initialSnapshot: EditProfileSnapshot? = null,
    val nameError: EditProfileFieldError? = null,
    val phoneError: EditProfileFieldError? = null,
    val bioError: EditProfileFieldError? = null,
    val error: AuthError? = null
) {
    val snapshot: EditProfileSnapshot
        get() = EditProfileSnapshot(name, phone, bio, photoFile != null)

    val isDirty: Boolean
        get() = initialSnapshot != null && initialSnapshot != snapshot
}

data class EditProfileSnapshot(
    val name: String,
    val phone: String,
    val bio: String,
    val hasNewPhoto: Boolean
)
