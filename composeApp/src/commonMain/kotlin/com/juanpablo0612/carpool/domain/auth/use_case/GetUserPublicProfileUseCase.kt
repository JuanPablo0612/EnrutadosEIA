package com.juanpablo0612.carpool.domain.auth.use_case

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class GetUserPublicProfileUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(userId: String): Result<PublicProfile> =
        authRepository.getPublicProfile(userId)
}
