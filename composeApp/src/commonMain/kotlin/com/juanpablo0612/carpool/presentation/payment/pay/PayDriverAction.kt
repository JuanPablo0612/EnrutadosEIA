package com.juanpablo0612.carpool.presentation.payment.pay

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod

sealed class PayDriverAction {
    /** The screen copies to the clipboard (a platform API) and reports it here. */
    data class OnKeyCopied(val methodId: String) : PayDriverAction()
    /** Sent once the screen has gallery access (a platform permission). */
    data class OnSaveQr(val method: PaymentMethod.QrCode) : PayDriverAction()
    data object OnGalleryPermissionDenied : PayDriverAction()
    data object OnDismissError : PayDriverAction()
    data object OnRetry : PayDriverAction()
    data object OnBackClick : PayDriverAction()
}
