package com.juanpablo0612.carpool.data.payment.datasource

import com.juanpablo0612.carpool.data.payment.model.PaymentMethodDto
import com.juanpablo0612.carpool.data.vehicle.datasource.uploadJpeg
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebasePaymentMethodRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : PaymentMethodRemoteDataSource {

    private fun methods(userId: String): CollectionReference =
        firestore.collection(USERS).document(userId).collection(PAYMENT_METHODS)

    override fun newPaymentMethodId(userId: String): String = methods(userId).document.id

    override fun observePaymentMethods(userId: String): Flow<List<PaymentMethodDto>> =
        methods(userId).snapshots.map { snapshot ->
            snapshot.documents.map { it.data(PaymentMethodDto.serializer()) }
        }

    override suspend fun getPaymentMethods(userId: String): List<PaymentMethodDto> =
        methods(userId).get().documents.map { it.data(PaymentMethodDto.serializer()) }

    override suspend fun savePaymentMethod(userId: String, method: PaymentMethodDto) {
        methods(userId).document(method.id).set(PaymentMethodDto.serializer(), method)
    }

    override suspend fun deletePaymentMethod(userId: String, methodId: String) {
        methods(userId).document(methodId).delete()
    }

    override suspend fun uploadQrImage(userId: String, methodId: String, jpegBytes: ByteArray): String {
        val ref = storage.reference.child(qrPath(userId, methodId))
        ref.uploadJpeg(jpegBytes)
        return ref.getDownloadUrl()
    }

    override suspend fun deleteQrImage(userId: String, methodId: String) {
        storage.reference.child(qrPath(userId, methodId)).delete()
    }

    // Under users/{uid}/ so the account purge, which deletes that prefix, removes it too.
    private fun qrPath(userId: String, methodId: String) = "$USERS/$userId/payment/$methodId.jpg"

    companion object {
        private const val USERS = "users"
        private const val PAYMENT_METHODS = "paymentMethods"
    }
}
