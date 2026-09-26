package com.juanpablo0612.carpool.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import com.juanpablo0612.carpool.presentation.navigation.NotificationDeepLink
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
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
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            notificationRepository.getNotifications(userId)
                .onEach { notifications ->
                    _state.update { it.copy(notifications = notifications, isLoading = false, error = null) }
                }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.toNotificationError()) } }
                .collect {}
        }
    }

    // The list is already live via the persistent collector started in init — pull-to-refresh
    // just needs a one-shot fetch to resolve the refreshing indicator, not a second subscription.
    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            runCatching { notificationRepository.getNotifications(userId).first() }
                .onSuccess { notifications -> _state.update { it.copy(notifications = notifications) } }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun onAction(action: NotificationsAction) {
        when (action) {
            is NotificationsAction.OnNotificationClick -> {
                viewModelScope.launch {
                    notificationRepository.markRead(userId, action.notification.id)
                    val notification = action.notification
                    val deepLink = NotificationDeepLink.forNotification(notification.type, notification.params)
                    if (deepLink != null) {
                        _events.emit(NotificationsEvent.NavigateTo(deepLink))
                    }
                }
            }
            is NotificationsAction.OnSwipeToDelete -> _state.update { it.copy(pendingDeleteId = action.id) }
            NotificationsAction.OnDismissDeleteConfirm -> _state.update { it.copy(pendingDeleteId = null) }
            NotificationsAction.OnConfirmDelete -> {
                val id = _state.value.pendingDeleteId ?: return
                _state.update { it.copy(pendingDeleteId = null) }
                viewModelScope.launch {
                    notificationRepository.delete(userId, id)
                        .onFailure { _state.update { it.copy(actionError = NotificationActionError.DeleteFailed) } }
                }
            }
            NotificationsAction.OnClearAllClick -> _state.update { it.copy(showClearAllDialog = true) }
            NotificationsAction.OnClearAllDismissed -> _state.update { it.copy(showClearAllDialog = false) }
            NotificationsAction.OnClearAllConfirmed -> {
                _state.update { it.copy(showClearAllDialog = false) }
                viewModelScope.launch {
                    notificationRepository.clearAll(userId)
                        .onFailure { _state.update { it.copy(actionError = NotificationActionError.ClearAllFailed) } }
                }
            }
            NotificationsAction.OnRetry -> loadNotifications()
            NotificationsAction.Refresh -> refresh()
            NotificationsAction.OnDismissActionError -> _state.update { it.copy(actionError = null) }
            NotificationsAction.OnBackClick -> {
                viewModelScope.launch { _events.emit(NotificationsEvent.NavigateBack) }
            }
        }
    }
}
