package com.juanpablo0612.carpool.domain.auth.use_case

import com.juanpablo0612.carpool.core.result.AppResult
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean
    ): Result<Unit> {
        return repository.register(email, password, name, isPassenger, isDriver)
    }
}
