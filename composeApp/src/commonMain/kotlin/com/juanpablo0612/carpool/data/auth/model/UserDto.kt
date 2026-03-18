package com.juanpablo0612.carpool.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean
)
