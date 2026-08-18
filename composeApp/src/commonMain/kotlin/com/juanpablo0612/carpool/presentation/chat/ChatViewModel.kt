package com.juanpablo0612.carpool.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.chat.usecase.GetMessagesUseCase
import com.juanpablo0612.carpool.domain.chat.usecase.MarkMessagesReadUseCase
import com.juanpablo0612.carpool.domain.chat.usecase.SendMessageUseCase
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

class ChatViewModel(
    private val bookingId: String,
    private val otherPartyName: String,
    private val isReadOnly: Boolean,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesReadUseCase: MarkMessagesReadUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChatUiState(
            otherPartyName = otherPartyName,
            isReadOnly = isReadOnly,
            currentUserId = authRepository.getCurrentUserId() ?: ""
        )
    )
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events: SharedFlow<ChatEvent> = _events.asSharedFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            getMessagesUseCase(bookingId)
                .onEach { messages ->
                    _state.update { it.copy(messages = messages, isLoading = false) }
                    val userId = authRepository.getCurrentUserId() ?: return@onEach
                    markMessagesReadUseCase(bookingId, userId)
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect {}
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnInputChange -> _state.update { it.copy(inputText = action.text) }
            is ChatAction.OnQuickReplyClick -> sendText(action.text)
            ChatAction.OnSendClick -> {
                val text = _state.value.inputText.trim()
                if (text.isNotBlank()) sendText(text)
            }
            ChatAction.OnBackClick -> viewModelScope.launch { _events.emit(ChatEvent.NavigateBack) }
        }
    }

    private fun sendText(text: String) {
        val senderId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSending = true, inputText = "") }
            sendMessageUseCase(bookingId, senderId, text)
                .onFailure { _state.update { it.copy(isSending = false) } }
                .onSuccess { _state.update { it.copy(isSending = false) } }
        }
    }
}
