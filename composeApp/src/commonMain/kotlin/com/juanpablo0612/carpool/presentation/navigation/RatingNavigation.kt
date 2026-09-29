package com.juanpablo0612.carpool.presentation.navigation

import com.juanpablo0612.carpool.presentation.rating.RatingTarget

fun RatingTarget.toRatingRoute(): Route.PostTripRating = Route.PostTripRating(
    bookingId = bookingId,
    tripId = tripId,
    rateeId = rateeId,
    rateeName = rateeName,
    rateeIsDriver = rateeIsDriver,
    rateePhotoUrl = rateePhotoUrl,
    departureTime = departureTime,
    originName = originName,
    destinationName = destinationName,
)

fun Route.PostTripRating.toTarget(): RatingTarget = RatingTarget(
    bookingId = bookingId,
    tripId = tripId,
    rateeId = rateeId,
    rateeName = rateeName,
    rateePhotoUrl = rateePhotoUrl,
    rateeIsDriver = rateeIsDriver,
    departureTime = departureTime,
    originName = originName,
    destinationName = destinationName,
)
