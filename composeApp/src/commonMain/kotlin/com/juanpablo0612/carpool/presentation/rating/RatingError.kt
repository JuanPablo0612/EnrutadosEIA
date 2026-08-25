package com.juanpablo0612.carpool.presentation.rating

sealed class RatingError {
    data object Unknown : RatingError()
}
