package com.juanpablo0612.carpool.presentation.payment.manage

sealed class PaymentMethodsEvent {
    data object NavigateBack : PaymentMethodsEvent()
}
