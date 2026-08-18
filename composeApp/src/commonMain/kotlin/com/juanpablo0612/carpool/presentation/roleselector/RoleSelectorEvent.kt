package com.juanpablo0612.carpool.presentation.roleselector

sealed class RoleSelectorEvent {
    data object NavigateToDriver : RoleSelectorEvent()
    data object NavigateToPassenger : RoleSelectorEvent()
}
