package com.juanpablo0612.carpool.data.auth.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import dev.gitlive.firebase.auth.*

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            remoteDataSource.signIn(email, password)
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(e.toAppException())
        } catch (e: Exception) {
            Result.failure(AppException.AuthException.Unknown)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        isPassenger: Boolean,
        isDriver: Boolean
    ): Result<Unit> {
        return try {
            remoteDataSource.signUp(email, password, name, isPassenger, isDriver)
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(e.toAppException())
        } catch (e: Exception) {
            Result.failure(AppException.AuthException.Unknown)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            remoteDataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.AuthException.Unknown)
        }
    }

    private fun FirebaseAuthException.toAppException(): AppException.AuthException {
        return when (this) {
            is FirebaseAuthInvalidCredentialsException -> AppException.AuthException.InvalidCredentials
            is FirebaseAuthInvalidUserException -> AppException.AuthException.UserNotFound
            is FirebaseAuthUserCollisionException -> AppException.AuthException.EmailAlreadyInUse
            is FirebaseAuthWeakPasswordException -> AppException.AuthException.WeakPassword
            else -> AppException.AuthException.Unknown
        }
    }
}
