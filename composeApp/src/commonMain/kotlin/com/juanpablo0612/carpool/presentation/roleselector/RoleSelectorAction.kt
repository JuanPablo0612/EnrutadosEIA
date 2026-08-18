package com.juanpablo0612.carpool.presentation.roleselector

sealed class RoleSelectorAction {
    data object OnSelectDriver : RoleSelectorAction()
    data object OnSelectPassenger : RoleSelectorAction()
    data class OnToggleRememberChoice(val checked: Boolean) : RoleSelectorAction()
}
