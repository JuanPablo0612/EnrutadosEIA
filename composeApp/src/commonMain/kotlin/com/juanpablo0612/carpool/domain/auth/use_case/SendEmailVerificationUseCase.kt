package com.juanpablo0612.carpool.domain.auth.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class SendEmailVerificationUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.sendEmailVerification()
}
