package com.juanpablo0612.carpool.presentation.profile.edit

sealed class EditProfileAction {
    data class OnNameChange(val name: String) : EditProfileAction()
    data class OnPhoneChange(val phone: String) : EditProfileAction()
    data class OnBioChange(val bio: String) : EditProfileAction()
    data object OnSaveClick : EditProfileAction()
}
