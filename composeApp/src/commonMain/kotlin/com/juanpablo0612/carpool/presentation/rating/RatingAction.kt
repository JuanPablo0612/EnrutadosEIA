package com.juanpablo0612.carpool.presentation.rating

import com.juanpablo0612.carpool.domain.rating.model.RatingChip

sealed class RatingAction {
    data class OnStarSelect(val stars: Int) : RatingAction()
    data class OnChipToggle(val chip: RatingChip) : RatingAction()
    data class OnCommentChange(val comment: String) : RatingAction()
    data object OnSubmit : RatingAction()
    data object OnSkip : RatingAction()
}
