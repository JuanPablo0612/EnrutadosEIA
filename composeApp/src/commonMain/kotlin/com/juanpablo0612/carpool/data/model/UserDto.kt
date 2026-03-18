package com.juanpablo0612.carpool.data.model

import com.juanpablo0612.carpool.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        isEmailVerified = isEmailVerified
    )
}
