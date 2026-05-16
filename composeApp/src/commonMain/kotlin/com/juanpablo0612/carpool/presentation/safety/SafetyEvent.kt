package com.juanpablo0612.carpool.presentation.safety

sealed class SafetyEvent {
    data object NavigateBack : SafetyEvent()
}
