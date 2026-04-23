package com.juanpablo0612.carpool.domain.auth.use_case

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<User> = authRepository.getCurrentUser()
}
