package com.juanpablo0612.carpool.domain.payment.model

/**
 * One way a driver takes their passengers' contributions. Each kind is a subtype, so adding one
 * (Nequi, Daviplata, a bank account) means a new subtype plus its [PaymentMethodType], draft and
 * DTO mapping; `when` over the sealed hierarchy then points at every screen that must show it.
 */
sealed interface PaymentMethod {
    val id: String
    val type: PaymentMethodType

    /** A Bre-B key: a phone number, ID number, email or an alphanumeric `@alias`. */
    data class BreBKey(override val id: String, val key: String) : PaymentMethod {
        override val type: PaymentMethodType get() = PaymentMethodType.BreBKey
    }

    /** A picture of the QR code the driver's bank app generates for receiving transfers. */
    data class QrCode(override val id: String, val imageUrl: String) : PaymentMethod {
        override val type: PaymentMethodType get() = PaymentMethodType.QrCode
    }
}

sealed interface PaymentMethodType {
    data object BreBKey : PaymentMethodType
    data object QrCode : PaymentMethodType

    companion object {
        /** Every kind a driver can add, in the order the picker offers them. */
        val all: List<PaymentMethodType> = listOf(BreBKey, QrCode)
    }
}
