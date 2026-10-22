package com.juanpablo0612.carpool.data.payment.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.payment.datasource.GalleryDataSource
import com.juanpablo0612.carpool.data.payment.datasource.PaymentMethodRemoteDataSource
import com.juanpablo0612.carpool.data.payment.model.PaymentMethodDto
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class PaymentMethodRepositoryImpl(
    private val remoteDataSource: PaymentMethodRemoteDataSource,
    private val galleryDataSource: GalleryDataSource,
) : PaymentMethodRepository {

    override fun observePaymentMethods(userId: String): Flow<List<PaymentMethod>> =
        remoteDataSource.observePaymentMethods(userId)
            .map { it.toDomainList() }
            .catch { throw AppException.PaymentException.Unknown }

    override suspend fun getPaymentMethods(userId: String): Result<List<PaymentMethod>> = runCatchingPayment {
        remoteDataSource.getPaymentMethods(userId).toDomainList()
    }

    override suspend fun addPaymentMethod(userId: String, draft: PaymentMethodDraft): Result<Unit> = runCatchingPayment {
        val id = remoteDataSource.newPaymentMethodId(userId)
        val base = PaymentMethodDto(
            id = id,
            type = PaymentMethodDto.typeOf(draft.type),
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        val dto = when (draft) {
            is PaymentMethodDraft.BreBKey -> base.copy(key = draft.key)
            is PaymentMethodDraft.QrCode -> {
                // A lighter compression than for photos: the code must stay scannable.
                val jpeg = FileKit.compressImage(bytes = draft.imageBytes, quality = 90, imageFormat = ImageFormat.JPEG)
                base.copy(imageUrl = remoteDataSource.uploadQrImage(userId, id, jpeg))
            }
        }
        remoteDataSource.savePaymentMethod(userId, dto)
    }

    override suspend fun deletePaymentMethod(userId: String, method: PaymentMethod): Result<Unit> = runCatchingPayment {
        remoteDataSource.deletePaymentMethod(userId, method.id)
        if (method is PaymentMethod.QrCode) {
            // The document is what passengers see; a leftover image is harmless and is removed
            // with the account anyway.
            runCatching { remoteDataSource.deleteQrImage(userId, method.id) }
        }
    }

    override suspend fun saveQrCodeToGallery(method: PaymentMethod.QrCode, fileName: String): Result<Unit> =
        runCatchingPayment { galleryDataSource.saveRemoteImage(method.imageUrl, fileName) }

    private fun List<PaymentMethodDto>.toDomainList(): List<PaymentMethod> =
        sortedBy { it.createdAt }.mapNotNull { it.toDomain() }

    private inline fun <T> runCatchingPayment(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        Result.failure(AppException.PaymentException.Unknown)
    }
}
