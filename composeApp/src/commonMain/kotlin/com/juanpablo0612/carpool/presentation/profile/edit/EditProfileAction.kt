package com.juanpablo0612.carpool.presentation.profile.edit

import io.github.vinceglb.filekit.PlatformFile

sealed class EditProfileAction {
    data class OnNameChange(val name: String) : EditProfileAction()
    data class OnPhoneChange(val phone: String) : EditProfileAction()
    data class OnBioChange(val bio: String) : EditProfileAction()
    data class OnPhotoSelected(val file: PlatformFile?) : EditProfileAction()
    data object OnSaveClick : EditProfileAction()
}
