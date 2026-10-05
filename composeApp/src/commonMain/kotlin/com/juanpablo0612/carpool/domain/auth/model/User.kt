package com.juanpablo0612.carpool.domain.auth.model

import com.juanpablo0612.carpool.domain.rating.model.RatingSummary

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
    val phone: PhoneNumber? = null,
    val photoUrl: String? = null,
    val bio: String? = null,
    /** Null until someone rates the user. */
    val rating: RatingSummary? = null,
)
