package com.juanpablo0612.carpool.presentation.role_selector

data class RoleSelectorUiState(
    val userName: String = "",
    val driverPendingCount: Int = 0,
    val rememberChoice: Boolean = false,
    val isLoading: Boolean = false
)
