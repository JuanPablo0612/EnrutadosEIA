package com.juanpablo0612.carpool.domain.auth.model

import com.juanpablo0612.carpool.domain.rating.model.RatingSummary

/**
 * Reduced, PII-free view of a [User] safe to hand to any signed-in user browsing trips or
 * booking requests (e.g. to show who's driving). Deliberately excludes email, phone, role
 * flags, and isEmailVerified, which must never be exposed to anyone other than the profile's
 * owner.
 */
data class PublicProfile(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val bio: String? = null,
    /** The rating aggregate is public: it is what lets passengers and drivers trust each other. */
    val rating: RatingSummary? = null,
)
