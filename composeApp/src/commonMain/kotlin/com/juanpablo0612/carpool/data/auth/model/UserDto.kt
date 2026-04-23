package com.juanpablo0612.carpool.data.auth.model

import com.juanpablo0612.carpool.domain.auth.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
    val isPassenger: Boolean,
    val isDriver: Boolean
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        name = name,
        isEmailVerified = isEmailVerified,
        isPassenger = isPassenger,
        isDriver = isDriver
    )
}
