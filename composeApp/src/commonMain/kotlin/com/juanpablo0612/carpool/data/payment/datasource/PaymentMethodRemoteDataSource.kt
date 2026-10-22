package com.juanpablo0612.carpool.data.payment.datasource

import com.juanpablo0612.carpool.data.payment.model.PaymentMethodDto
import kotlinx.coroutines.flow.Flow

interface PaymentMethodRemoteDataSource {
    fun newPaymentMethodId(userId: String): String
    fun observePaymentMethods(userId: String): Flow<List<PaymentMethodDto>>
    suspend fun getPaymentMethods(userId: String): List<PaymentMethodDto>
    suspend fun savePaymentMethod(userId: String, method: PaymentMethodDto)
    suspend fun deletePaymentMethod(userId: String, methodId: String)

    /** Uploads a JPEG QR image and returns its download URL. */
    suspend fun uploadQrImage(userId: String, methodId: String, jpegBytes: ByteArray): String
    suspend fun deleteQrImage(userId: String, methodId: String)
}
