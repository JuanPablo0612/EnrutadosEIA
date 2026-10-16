package com.juanpablo0612.carpool.domain.payment.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidator

/** Adds a payment method to the signed-in user's profile after validating it. */
class AddPaymentMethodUseCase(
    private val repository: PaymentMethodRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(draft: PaymentMethodDraft): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.PaymentException.NotAuthenticated)

        val normalized = PaymentMethodValidator.normalize(draft)
        val errors = PaymentMethodValidator.validate(normalized)
        if (errors.isNotEmpty()) return Result.failure(AppException.PaymentException.Invalid(errors))

        val existing = repository.getPaymentMethods(userId).getOrElse { return Result.failure(it) }
        if (existing.size >= PaymentMethodValidator.MAX_METHODS) {
            return Result.failure(AppException.PaymentException.LimitReached)
        }
        if (PaymentMethodValidator.isDuplicate(normalized, existing)) {
            return Result.failure(AppException.PaymentException.AlreadyAdded)
        }
        return repository.addPaymentMethod(userId, normalized)
    }
}
