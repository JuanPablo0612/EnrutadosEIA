package com.juanpablo0612.carpool.domain.auth.usecase

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class DeleteAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}
