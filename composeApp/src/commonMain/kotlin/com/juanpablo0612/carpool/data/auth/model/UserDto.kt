package com.juanpablo0612.carpool.data.auth.model

import com.juanpablo0612.carpool.domain.auth.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String = "",
    val email: String = "",
    val name: String? = null,
    val isEmailVerified: Boolean = false,
    val isPassenger: Boolean = false,
    val isDriver: Boolean = false,
    val phone: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        name = name,
        isEmailVerified = isEmailVerified,
        isPassenger = isPassenger,
        isDriver = isDriver,
        phone = phone,
        photoUrl = photoUrl,
        bio = bio
    )
}
