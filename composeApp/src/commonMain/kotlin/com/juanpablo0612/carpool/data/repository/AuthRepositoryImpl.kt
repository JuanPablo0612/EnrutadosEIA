package com.juanpablo0612.carpool.data.repository

import com.juanpablo0612.carpool.core.AppResult
import com.juanpablo0612.carpool.data.datasource.AuthRemoteDataSource
import com.juanpablo0612.carpool.data.model.toDomain
import com.juanpablo0612.carpool.domain.error.AuthError
import com.juanpablo0612.carpool.domain.model.User
import com.juanpablo0612.carpool.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User, AuthError> {
        return try {
            val userDto = remoteDataSource.signIn(email, password)
            AppResult.Success(userDto.toDomain())
        } catch (e: FirebaseAuthException) {
            AppResult.Error(mapFirebaseError(e))
        } catch (e: Exception) {
            AppResult.Error(AuthError.UnknownError)
        }
    }

    override suspend fun register(email: String, password: String): AppResult<User, AuthError> {
        return try {
            val userDto = remoteDataSource.signUp(email, password)
            AppResult.Success(userDto.toDomain())
        } catch (e: FirebaseAuthException) {
            AppResult.Error(mapFirebaseError(e))
        } catch (e: Exception) {
            AppResult.Error(AuthError.UnknownError)
        }
    }

    override suspend fun logout(): AppResult<Unit, AuthError> {
        return try {
            remoteDataSource.signOut()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AuthError.UnknownError)
        }
    }

    override fun observeAuthState(): Flow<User?> {
        return remoteDataSource.getAuthState().map { it?.toDomain() }
    }

    private fun mapFirebaseError(e: FirebaseAuthException): AuthError {
        // Simple mapping as gitlive-firebase might have different property names or we just use message matching
        val message = e.message ?: ""
        return when {
            message.contains("wrong-password", true) || message.contains("user-not-found", true) -> AuthError.InvalidCredentials
            message.contains("email-already-in-use", true) -> AuthError.EmailAlreadyInUse
            message.contains("network-request-failed", true) -> AuthError.NetworkError
            message.contains("weak-password", true) -> AuthError.WeakPassword
            else -> AuthError.UnknownError
        }
    }
}
