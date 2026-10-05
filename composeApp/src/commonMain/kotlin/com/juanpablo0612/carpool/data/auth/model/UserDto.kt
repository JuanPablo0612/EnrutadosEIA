package com.juanpablo0612.carpool.data.auth.model

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String = "",
    val email: String = "",
    val name: String? = null,
    val isEmailVerified: Boolean = false,
    /** Digits only, without "+"; null together with [phoneNumber] when the user has no phone. */
    val phoneCountryCode: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        name = name,
        isEmailVerified = isEmailVerified,
        phone = phoneOrNull(),
        photoUrl = photoUrl,
        bio = bio
    )

    // Only the fields safe to show to any signed-in user browsing trips/bookings — no email,
    // phone, or isEmailVerified. See PublicProfile.
    private fun phoneOrNull(): PhoneNumber? {
        val countryCode = phoneCountryCode?.takeIf { it.isNotBlank() } ?: return null
        val number = phoneNumber?.takeIf { it.isNotBlank() } ?: return null
        return PhoneNumber(countryCode = countryCode, number = number)
    }

    fun toPublicProfile(): PublicProfile = PublicProfile(
        id = id,
        name = name.orEmpty(),
        photoUrl = photoUrl,
        bio = bio
    )
}
