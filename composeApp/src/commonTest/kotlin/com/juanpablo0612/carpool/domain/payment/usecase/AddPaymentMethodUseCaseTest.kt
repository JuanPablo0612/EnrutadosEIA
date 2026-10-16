package com.juanpablo0612.carpool.domain.payment.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidationError
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidator
import com.juanpablo0612.carpool.domain.trip.usecase.FakeAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class FakePaymentMethodRepository(
    var existing: List<PaymentMethod> = emptyList(),
    private val readFails: Boolean = false,
) : PaymentMethodRepository {
    val added = mutableListOf<Pair<String, PaymentMethodDraft>>()

    override fun observePaymentMethods(userId: String): Flow<List<PaymentMethod>> = flowOf(existing)
    override suspend fun getPaymentMethods(userId: String): Result<List<PaymentMethod>> =
        if (readFails) Result.failure(AppException.PaymentException.Unknown) else Result.success(existing)
    override suspend fun addPaymentMethod(userId: String, draft: PaymentMethodDraft): Result<Unit> {
        added += userId to draft
        return Result.success(Unit)
    }
    override suspend fun deletePaymentMethod(userId: String, method: PaymentMethod): Result<Unit> = Result.success(Unit)
    override suspend fun saveQrCodeToGallery(method: PaymentMethod.QrCode, fileName: String): Result<Unit> = Result.success(Unit)
}

class AddPaymentMethodUseCaseTest {

    @Test
    fun aValidKeyIsSavedTrimmedForTheSignedInUser() = runTest {
        val repository = FakePaymentMethodRepository()
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository("u1"))(PaymentMethodDraft.BreBKey(" @carolina "))

        assertTrue(result.isSuccess)
        assertEquals(listOf("u1" to PaymentMethodDraft.BreBKey("@carolina")), repository.added)
    }

    @Test
    fun aSignedOutUserCannotAdd() = runTest {
        val repository = FakePaymentMethodRepository()
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository(null))(PaymentMethodDraft.BreBKey("@carolina"))

        assertEquals(AppException.PaymentException.NotAuthenticated, result.exceptionOrNull())
        assertTrue(repository.added.isEmpty())
    }

    @Test
    fun anInvalidDraftFailsWithItsErrors() = runTest {
        val repository = FakePaymentMethodRepository()
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository("u1"))(PaymentMethodDraft.BreBKey(""))

        val error = assertIs<AppException.PaymentException.Invalid>(result.exceptionOrNull())
        assertEquals(listOf(PaymentMethodValidationError.KeyRequired), error.errors)
        assertTrue(repository.added.isEmpty())
    }

    @Test
    fun theLimitStopsAnotherMethod() = runTest {
        val full = List(PaymentMethodValidator.MAX_METHODS) { PaymentMethod.BreBKey("$it", "key$it") }
        val repository = FakePaymentMethodRepository(existing = full)
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository("u1"))(PaymentMethodDraft.QrCode(byteArrayOf(1)))

        assertEquals(AppException.PaymentException.LimitReached, result.exceptionOrNull())
        assertTrue(repository.added.isEmpty())
    }

    @Test
    fun aRepeatedKeyIsRejected() = runTest {
        val repository = FakePaymentMethodRepository(existing = listOf(PaymentMethod.BreBKey("1", "@carolina")))
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository("u1"))(PaymentMethodDraft.BreBKey("@Carolina"))

        assertEquals(AppException.PaymentException.AlreadyAdded, result.exceptionOrNull())
    }

    @Test
    fun aFailedReadOfTheExistingMethodsFailsTheAdd() = runTest {
        val repository = FakePaymentMethodRepository(readFails = true)
        val result = AddPaymentMethodUseCase(repository, FakeAuthRepository("u1"))(PaymentMethodDraft.BreBKey("@carolina"))

        assertEquals(AppException.PaymentException.Unknown, result.exceptionOrNull())
        assertTrue(repository.added.isEmpty())
    }
}
