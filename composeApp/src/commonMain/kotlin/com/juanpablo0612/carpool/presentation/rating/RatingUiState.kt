package com.juanpablo0612.carpool.presentation.rating

import com.juanpablo0612.carpool.domain.rating.model.RatingChip

data class RatingUiState(
    val bookingId: String = "",
    val rateeId: String = "",
    val rateeName: String = "",
    val isDriver: Boolean = false,
    val selectedStars: Int = 0,
    val selectedChips: Set<RatingChip> = emptySet(),
    val comment: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    val availableChips: List<RatingChip>
        get() = if (isDriver) RatingChip.driverChips else RatingChip.passengerChips
}
