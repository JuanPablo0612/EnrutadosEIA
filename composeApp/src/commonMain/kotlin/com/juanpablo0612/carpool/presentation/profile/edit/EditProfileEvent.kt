package com.juanpablo0612.carpool.presentation.profile.edit

sealed class EditProfileEvent {
    data object SaveSuccess : EditProfileEvent()
}
