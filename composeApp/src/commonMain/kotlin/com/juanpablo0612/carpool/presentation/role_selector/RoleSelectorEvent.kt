package com.juanpablo0612.carpool.presentation.role_selector

sealed class RoleSelectorEvent {
    data object NavigateToDriver : RoleSelectorEvent()
    data object NavigateToPassenger : RoleSelectorEvent()
}
