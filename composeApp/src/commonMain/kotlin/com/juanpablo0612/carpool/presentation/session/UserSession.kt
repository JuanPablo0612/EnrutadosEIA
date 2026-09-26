package com.juanpablo0612.carpool.presentation.session

import com.juanpablo0612.carpool.domain.auth.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSession {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun setUser(user: User) {
        _user.value = user
    }

    fun clearSession() {
        _user.value = null
    }
}
