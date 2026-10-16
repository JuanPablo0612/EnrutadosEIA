package com.juanpablo0612.carpool.data.payment.model

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import kotlinx.serialization.Serializable

/**
 * A users/{uid}/paymentMethods/{id} document. One flat shape for every kind, told apart by
 * [type]; each kind fills only its own fields. firestore.rules lists the allowed types and keys.
 */
@Serializable
data class PaymentMethodDto(
    val id: String = "",
    val type: String = "",
    /** Bre-B only. */
    val key: String? = null,
    /** QR only: the Storage download URL of users/{uid}/payment/{id}.jpg. */
    val imageUrl: String? = null,
    val createdAt: Long = 0,
) {
    /**
     * Null for a kind this build doesn't know or a document missing its data, so an older app
     * skips a method added by a newer one instead of failing the whole list.
     */
    fun toDomain(): PaymentMethod? = when (type) {
        TYPE_BRE_B_KEY -> key?.takeIf { it.isNotBlank() }?.let { PaymentMethod.BreBKey(id = id, key = it) }
        TYPE_QR_CODE -> imageUrl?.takeIf { it.isNotBlank() }?.let { PaymentMethod.QrCode(id = id, imageUrl = it) }
        else -> null
    }

    companion object {
        const val TYPE_BRE_B_KEY = "BRE_B_KEY"
        const val TYPE_QR_CODE = "QR_CODE"

        fun typeOf(type: PaymentMethodType): String = when (type) {
            PaymentMethodType.BreBKey -> TYPE_BRE_B_KEY
            PaymentMethodType.QrCode -> TYPE_QR_CODE
        }
    }
}
