package com.juanpablo0612.carpool.data.auth.repository

import com.juanpablo0612.carpool.core.result.AppResult
import com.juanpablo0612.carpool.data.auth.mapper.mapFirebaseError
import com.juanpablo0612.carpool.data.auth.mapper.toDomain
import com.juanpablo0612.carpool.data.auth.remote.AuthRemoteDataSource
import com.juanpablo0612.carpool.domain.auth.model.AuthError
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
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
}
