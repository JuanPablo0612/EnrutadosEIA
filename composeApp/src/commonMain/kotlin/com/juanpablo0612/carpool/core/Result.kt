package com.juanpablo0612.carpool.core

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

sealed class AppResult<out T, out E> {
    data class Success<out T>(val data: T) : AppResult<T, Nothing>()
    data class Error<out E>(val error: E) : AppResult<Nothing, E>()
}
