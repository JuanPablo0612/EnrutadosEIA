package com.juanpablo0612.carpool.domain.usecase

import com.juanpablo0612.carpool.core.AppResult
import com.juanpablo0612.carpool.domain.error.AuthError
import com.juanpablo0612.carpool.domain.model.User
import com.juanpablo0612.carpool.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AppResult<User, AuthError> {
        if (email.isBlank() || password.isBlank()) {
            return AppResult.Error(AuthError.InvalidCredentials)
        }
        return repository.login(email.trim(), password)
    }
}
