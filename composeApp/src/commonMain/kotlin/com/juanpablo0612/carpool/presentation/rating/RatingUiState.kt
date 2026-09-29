package com.juanpablo0612.carpool.presentation.rating

import com.juanpablo0612.carpool.domain.rating.model.RatingChip

data class RatingUiState(
    val target: RatingTarget,
    val selectedStars: Int = 0,
    val selectedChips: Set<RatingChip> = emptySet(),
    val comment: String = "",
    /** True while checking whether this booking was already rated, so the form doesn't flash. */
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val alreadyRated: Boolean = false,
    val error: RatingError? = null,
) {
    val availableChips: List<RatingChip>
        get() = if (target.rateeIsDriver) RatingChip.driverChips else RatingChip.passengerChips

    val canSubmit: Boolean
        get() = selectedStars > 0 && !isSubmitting && error != RatingError.AlreadyRated
}
