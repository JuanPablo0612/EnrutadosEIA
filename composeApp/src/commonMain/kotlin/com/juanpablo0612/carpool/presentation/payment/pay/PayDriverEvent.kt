package com.juanpablo0612.carpool.presentation.payment.pay

sealed class PayDriverEvent {
    data object NavigateBack : PayDriverEvent()
}
