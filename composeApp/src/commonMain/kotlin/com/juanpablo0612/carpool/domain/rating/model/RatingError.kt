package com.juanpablo0612.carpool.domain.rating.model

sealed class RatingError {
    data object Unknown : RatingError()
}
