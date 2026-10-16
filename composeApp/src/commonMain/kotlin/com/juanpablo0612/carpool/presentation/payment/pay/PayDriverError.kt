package com.juanpablo0612.carpool.presentation.payment.pay

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_payment_gallery_permission
import enrutadoseia.composeapp.generated.resources.error_payment_qr_save_failed
import org.jetbrains.compose.resources.StringResource

sealed class PayDriverError {
    data object SaveQrFailed : PayDriverError()
    data object GalleryPermissionDenied : PayDriverError()

    fun asStringResource(): StringResource = when (this) {
        SaveQrFailed -> Res.string.error_payment_qr_save_failed
        GalleryPermissionDenied -> Res.string.error_payment_gallery_permission
    }
}
