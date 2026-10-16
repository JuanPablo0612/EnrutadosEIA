package com.juanpablo0612.carpool.presentation.payment.manage

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_payment_already_added
import enrutadoseia.composeapp.generated.resources.error_payment_delete_failed
import enrutadoseia.composeapp.generated.resources.error_payment_limit_reached
import enrutadoseia.composeapp.generated.resources.error_payment_save_failed
import org.jetbrains.compose.resources.StringResource

sealed class PaymentMethodsError {
    data object SaveFailed : PaymentMethodsError()
    data object DeleteFailed : PaymentMethodsError()
    data object LimitReached : PaymentMethodsError()
    data object AlreadyAdded : PaymentMethodsError()

    fun asStringResource(): StringResource = when (this) {
        SaveFailed -> Res.string.error_payment_save_failed
        DeleteFailed -> Res.string.error_payment_delete_failed
        LimitReached -> Res.string.error_payment_limit_reached
        AlreadyAdded -> Res.string.error_payment_already_added
    }
}
