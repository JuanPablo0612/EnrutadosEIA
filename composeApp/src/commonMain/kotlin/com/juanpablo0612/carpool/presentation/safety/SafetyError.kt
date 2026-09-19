package com.juanpablo0612.carpool.presentation.safety

sealed class SafetyError {
    data object MaxContactsReached : SafetyError()
    data object Unknown : SafetyError()
}
