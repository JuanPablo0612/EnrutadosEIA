package com.juanpablo0612.carpool.domain.payment.repository

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import kotlinx.coroutines.flow.Flow

interface PaymentMethodRepository {
    /** The user's methods, oldest first, kept current. Fails with `PaymentException.Unknown`. */
    fun observePaymentMethods(userId: String): Flow<List<PaymentMethod>>

    suspend fun getPaymentMethods(userId: String): Result<List<PaymentMethod>>

    /** Saves an already validated [draft], uploading its image when it has one. */
    suspend fun addPaymentMethod(userId: String, draft: PaymentMethodDraft): Result<Unit>

    suspend fun deletePaymentMethod(userId: String, method: PaymentMethod): Result<Unit>

    /** Downloads the QR image and saves it to the device's photo gallery. */
    suspend fun saveQrCodeToGallery(method: PaymentMethod.QrCode, fileName: String): Result<Unit>
}
