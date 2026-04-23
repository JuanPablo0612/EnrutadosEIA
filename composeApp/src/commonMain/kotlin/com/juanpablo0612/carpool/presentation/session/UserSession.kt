package com.juanpablo0612.carpool.presentation.session

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSession {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _activeRole = MutableStateFlow<UserRole?>(null)
    val activeRole = _activeRole.asStateFlow()

    fun setUser(user: User) {
        _user.value = user
    }

    fun setActiveRole(role: UserRole) {
        _activeRole.value = role
    }

    fun setSession(user: User, role: UserRole) {
        _user.value = user
        _activeRole.value = role
    }

    fun clearSession() {
        _user.value = null
        _activeRole.value = null
    }
}
