package com.juanpablo0612.carpool.presentation.payment.pay

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.trip.model.TripDriver

data class PayDriverUiState(
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val driver: TripDriver = TripDriver(),
    /** The trip's agreed contribution; null or 0 when the trip is free. */
    val contribution: Int? = null,
    val methods: List<PaymentMethod> = emptyList(),
    /** The key just copied, confirmed inline for a moment. */
    val copiedMethodId: String? = null,
    val savingQrId: String? = null,
    val savedQrId: String? = null,
    val error: PayDriverError? = null,
)
