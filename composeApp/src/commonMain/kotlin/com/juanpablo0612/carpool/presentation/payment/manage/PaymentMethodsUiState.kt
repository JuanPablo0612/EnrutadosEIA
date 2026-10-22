package com.juanpablo0612.carpool.presentation.payment.manage

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidationError
import com.juanpablo0612.carpool.domain.payment.validation.PaymentMethodValidator
import io.github.vinceglb.filekit.PlatformFile

data class PaymentMethodsUiState(
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val methods: List<PaymentMethod> = emptyList(),
    val showTypePicker: Boolean = false,
    /** The method being added, shown in a sheet; null when none is. */
    val editor: PaymentMethodEditor? = null,
    val isSaving: Boolean = false,
    val methodToDelete: PaymentMethod? = null,
    val error: PaymentMethodsError? = null,
) {
    val canAddMore: Boolean
        get() = methods.size < PaymentMethodValidator.MAX_METHODS
}

/** The form for one kind of method. A new kind adds its own editor here. */
sealed class PaymentMethodEditor {
    abstract val fieldError: PaymentMethodValidationError?

    data class BreBKey(
        val key: String = "",
        override val fieldError: PaymentMethodValidationError? = null,
    ) : PaymentMethodEditor()

    data class QrCode(
        val imageFile: PlatformFile? = null,
        override val fieldError: PaymentMethodValidationError? = null,
    ) : PaymentMethodEditor()
}
