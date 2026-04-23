package com.juanpablo0612.carpool.domain.auth.model

sealed class UserRole {
    data object Passenger : UserRole()
    data object Driver : UserRole()
}
