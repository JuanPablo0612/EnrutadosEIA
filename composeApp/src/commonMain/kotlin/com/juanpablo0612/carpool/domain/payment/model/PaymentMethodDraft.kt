package com.juanpablo0612.carpool.domain.payment.model

/** A payment method the driver is adding, before it has an id or an uploaded image. */
sealed interface PaymentMethodDraft {
    val type: PaymentMethodType

    data class BreBKey(val key: String) : PaymentMethodDraft {
        override val type: PaymentMethodType get() = PaymentMethodType.BreBKey
    }

    /** Not a data class: an array's equality is identity, which would make `==` misleading. */
    class QrCode(val imageBytes: ByteArray) : PaymentMethodDraft {
        override val type: PaymentMethodType get() = PaymentMethodType.QrCode
    }
}
