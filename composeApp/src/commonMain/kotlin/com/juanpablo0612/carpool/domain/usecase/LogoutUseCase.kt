package com.juanpablo0612.carpool.domain.usecase

import com.juanpablo0612.carpool.core.AppResult
import com.juanpablo0612.carpool.domain.error.AuthError
import com.juanpablo0612.carpool.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AppResult<Unit, AuthError> {
        return repository.logout()
    }
}
