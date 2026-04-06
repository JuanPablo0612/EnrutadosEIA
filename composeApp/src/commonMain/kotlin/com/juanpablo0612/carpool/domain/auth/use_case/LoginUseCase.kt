package com.juanpablo0612.carpool.domain.auth.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.login(email, password)
    }
}
