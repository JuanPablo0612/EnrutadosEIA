package com.juanpablo0612.carpool.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.notification.use_case.ClearAllNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.DeleteNotificationUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.GetNotificationsUseCase
import com.juanpablo0612.carpool.domain.notification.use_case.MarkNotificationReadUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val clearAllNotificationsUseCase: ClearAllNotificationsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<NotificationsEvent>()
    val events: SharedFlow<NotificationsEvent> = _events.asSharedFlow()

    private val userId = authRepository.getCurrentUserId() ?: ""

    init {
        if (userId.isNotBlank()) loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase(userId)
                .onEach { notifications ->
                    _state.update { it.copy(notifications = notifications, isLoading = false) }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: NotificationsAction) {
        when (action) {
            is NotificationsAction.OnNotificationClick -> {
                viewModelScope.launch {
                    markNotificationReadUseCase(action.notification.id)
                    val deepLink = action.notification.deepLink
                    if (!deepLink.isNullOrBlank()) {
                        _events.emit(NotificationsEvent.NavigateTo(deepLink))
                    }
                }
            }
            is NotificationsAction.OnDismiss -> {
                viewModelScope.launch { deleteNotificationUseCase(action.id) }
            }
            NotificationsAction.OnClearAll -> {
                viewModelScope.launch { clearAllNotificationsUseCase(userId) }
            }
            NotificationsAction.OnBackClick -> {
                viewModelScope.launch { _events.emit(NotificationsEvent.NavigateBack) }
            }
        }
    }
}
