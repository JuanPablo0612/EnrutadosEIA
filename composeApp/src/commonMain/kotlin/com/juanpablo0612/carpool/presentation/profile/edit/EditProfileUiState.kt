package com.juanpablo0612.carpool.presentation.profile.edit

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber
import com.juanpablo0612.carpool.domain.auth.validation.ValidationError
import com.juanpablo0612.carpool.presentation.auth.AuthError
import io.github.vinceglb.filekit.PlatformFile

data class EditProfileUiState(
    val name: String = "",
    val phoneCountryCode: String = PhoneNumber.DEFAULT_COUNTRY_CODE,
    val phoneNumber: String = "",
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
    val phoneCountryCodeError: ValidationError? = null,
    val phoneNumberError: ValidationError? = null,
    val bioError: EditProfileFieldError? = null,
    val error: AuthError? = null
) {
    val snapshot: EditProfileSnapshot
        get() = EditProfileSnapshot(name, phoneCountryCode, phoneNumber, bio, photoFile != null)

    val isDirty: Boolean
        get() = initialSnapshot != null && initialSnapshot != snapshot
}

data class EditProfileSnapshot(
    val name: String,
    val phoneCountryCode: String,
    val phoneNumber: String,
    val bio: String,
    val hasNewPhoto: Boolean
)
