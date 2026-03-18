package com.juanpablo0612.carpool.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean
)
