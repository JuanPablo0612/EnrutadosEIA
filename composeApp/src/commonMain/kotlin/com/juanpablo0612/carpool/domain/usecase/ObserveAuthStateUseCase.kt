package com.juanpablo0612.carpool.domain.usecase

import com.juanpablo0612.carpool.domain.model.User
import com.juanpablo0612.carpool.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<User?> {
        return repository.observeAuthState()
    }
}
