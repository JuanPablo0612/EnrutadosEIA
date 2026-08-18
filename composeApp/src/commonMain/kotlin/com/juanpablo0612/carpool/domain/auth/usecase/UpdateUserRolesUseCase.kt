package com.juanpablo0612.carpool.domain.auth.usecase

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class UpdateUserRolesUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(isDriver: Boolean, isPassenger: Boolean): Result<User> =
        repository.updateRoles(isDriver, isPassenger)
}
