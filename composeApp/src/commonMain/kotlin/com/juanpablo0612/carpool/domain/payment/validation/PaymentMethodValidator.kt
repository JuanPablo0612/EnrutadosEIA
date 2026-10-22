package com.juanpablo0612.carpool.domain.payment.validation

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft

sealed class PaymentMethodValidationError {
    data object KeyRequired : PaymentMethodValidationError()
    data object KeyHasSpaces : PaymentMethodValidationError()
    data object KeyTooLong : PaymentMethodValidationError()
    data object ImageRequired : PaymentMethodValidationError()
}

/** Checks a payment method before saving it. Pure, so it is shared by the form and the use case. */
object PaymentMethodValidator {
    /**
     * Bre-B keys are a phone, an ID number, an email or an alias of up to 20 characters; 50 leaves
     * room for long emails. firestore.rules enforces the same cap.
     */
    const val BRE_B_KEY_MAX_LENGTH = 50

    /** Enough for every method a driver could reasonably offer, small enough to show on one screen. */
    const val MAX_METHODS = 5

    /** The draft as it will be stored: surrounding whitespace in a typed key is never intended. */
    fun normalize(draft: PaymentMethodDraft): PaymentMethodDraft = when (draft) {
        is PaymentMethodDraft.BreBKey -> draft.copy(key = draft.key.trim())
        is PaymentMethodDraft.QrCode -> draft
    }

    /** Validates an already [normalize]d draft. */
    fun validate(draft: PaymentMethodDraft): List<PaymentMethodValidationError> = when (draft) {
        is PaymentMethodDraft.BreBKey -> validateBreBKey(draft.key)
        is PaymentMethodDraft.QrCode ->
            if (draft.imageBytes.isEmpty()) listOf(PaymentMethodValidationError.ImageRequired) else emptyList()
    }

    private fun validateBreBKey(key: String): List<PaymentMethodValidationError> = when {
        key.isEmpty() -> listOf(PaymentMethodValidationError.KeyRequired)
        key.any { it.isWhitespace() } -> listOf(PaymentMethodValidationError.KeyHasSpaces)
        key.length > BRE_B_KEY_MAX_LENGTH -> listOf(PaymentMethodValidationError.KeyTooLong)
        else -> emptyList()
    }

    /** Whether [draft] repeats a key the driver already saved. Images are never compared. */
    fun isDuplicate(draft: PaymentMethodDraft, existing: List<PaymentMethod>): Boolean = when (draft) {
        is PaymentMethodDraft.BreBKey -> existing.any {
            it is PaymentMethod.BreBKey && it.key.equals(draft.key, ignoreCase = true)
        }
        is PaymentMethodDraft.QrCode -> false
    }
}
