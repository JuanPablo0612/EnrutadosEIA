package com.juanpablo0612.carpool.presentation.payment

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidationError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_payment_bre_b_key_has_spaces
import enrutadoseia.composeapp.generated.resources.error_payment_bre_b_key_required
import enrutadoseia.composeapp.generated.resources.error_payment_bre_b_key_too_long
import enrutadoseia.composeapp.generated.resources.error_payment_qr_required
import enrutadoseia.composeapp.generated.resources.key_24px
import enrutadoseia.composeapp.generated.resources.payment_type_bre_b_key
import enrutadoseia.composeapp.generated.resources.payment_type_bre_b_key_description
import enrutadoseia.composeapp.generated.resources.payment_type_qr_code
import enrutadoseia.composeapp.generated.resources.payment_type_qr_code_description
import enrutadoseia.composeapp.generated.resources.qr_code_2_24px
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** Name, explanation and icon of each payment method kind, shared by the driver's and passenger's screens. */
val PaymentMethodType.label: StringResource
    get() = when (this) {
        PaymentMethodType.BreBKey -> Res.string.payment_type_bre_b_key
        PaymentMethodType.QrCode -> Res.string.payment_type_qr_code
    }

val PaymentMethodType.description: StringResource
    get() = when (this) {
        PaymentMethodType.BreBKey -> Res.string.payment_type_bre_b_key_description
        PaymentMethodType.QrCode -> Res.string.payment_type_qr_code_description
    }

val PaymentMethodType.icon: DrawableResource
    get() = when (this) {
        PaymentMethodType.BreBKey -> Res.drawable.key_24px
        PaymentMethodType.QrCode -> Res.drawable.qr_code_2_24px
    }

fun PaymentMethodValidationError.asStringResource(): StringResource = when (this) {
    PaymentMethodValidationError.KeyRequired -> Res.string.error_payment_bre_b_key_required
    PaymentMethodValidationError.KeyHasSpaces -> Res.string.error_payment_bre_b_key_has_spaces
    PaymentMethodValidationError.KeyTooLong -> Res.string.error_payment_bre_b_key_too_long
    PaymentMethodValidationError.ImageRequired -> Res.string.error_payment_qr_required
}
