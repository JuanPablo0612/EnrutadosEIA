package com.juanpablo0612.carpool.domain.auth.usecase

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository

class UpdateProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        phone: String?,
        bio: String?,
        photoUrl: String? = null
    ): Result<User> = repository.updateProfile(name, phone, bio, photoUrl)
}
