package com.juanpablo0612.carpool.data.auth.model

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.rating.model.RatingSummary
import kotlinx.serialization.Serializable

/**
 * The rating aggregate the onRatingCreated function keeps on users/{uid}. Integers, so the
 * average is computed on read rather than stored.
 *
 * Kept out of [UserDto] on purpose: the rules reject any client write of these fields, and the
 * Firestore encoder writes default values, so carrying them in the DTO the app writes at sign-up
 * would make that write fail.
 */
@Serializable
data class UserRatingDto(
    val ratingSum: Long = 0,
    val ratingCount: Long = 0,
)

/** A users/{uid} document as read: the fields the app writes plus the server-owned rating. */
data class UserDocument(
    val profile: UserDto,
    val rating: UserRatingDto,
) {
    fun toDomain(): User = profile.toDomain().copy(rating = rating.toSummary())

    fun toPublicProfile(): PublicProfile = profile.toPublicProfile().copy(rating = rating.toSummary())

    private fun UserRatingDto.toSummary(): RatingSummary? = RatingSummary.of(ratingSum, ratingCount)
}
