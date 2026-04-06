package com.juanpablo0612.carpool.domain.auth.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
    val isPassenger: Boolean,
    val isDriver: Boolean
)
