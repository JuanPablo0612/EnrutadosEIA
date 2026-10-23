package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val deleteAccountNameInput: String = "",
    val isDeleting: Boolean = false,
    val deleteAccountError: AuthError? = null,
    /** Null while loading or after a failed load; the card then shows a neutral fallback. */
    val vehicleSummary: VehicleSummary? = null,
    /** Null while loading or after a failed load; the card then shows a neutral fallback. */
    val paymentSummary: PaymentSummary? = null,
)
