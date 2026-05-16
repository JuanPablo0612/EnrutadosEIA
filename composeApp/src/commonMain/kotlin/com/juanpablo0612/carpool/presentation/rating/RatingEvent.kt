package com.juanpablo0612.carpool.presentation.rating

sealed class RatingEvent {
    data object RatingSubmitted : RatingEvent()
    data object Skipped : RatingEvent()
}
