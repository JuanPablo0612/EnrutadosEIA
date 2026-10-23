package com.juanpablo0612.carpool.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import com.juanpablo0612.carpool.domain.chat.usecase.SendMessageUseCase
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
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
import kotlin.time.Clock

class ChatViewModel(
    private val bookingId: String,
    private val tripId: String,
    private val otherPartyName: String,
    private val isReadOnly: Boolean,
    private val chatRepository: ChatRepository,
    private val sendMessageUseCase: SendMessageUseCase,
    private val authRepository: AuthRepository,
    private val tripRepository: TripRepository,
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
        observeTripStatus()
    }

    private fun loadMessages() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            chatRepository.getMessages(bookingId)
                .onEach { messages ->
                    _state.update { it.copy(messages = messages, isLoading = false, error = null) }
                    val userId = authRepository.getCurrentUserId() ?: return@onEach
                    chatRepository.markMessagesRead(bookingId, userId)
                }
                .catch { _state.update { it.copy(isLoading = false, error = ChatError.Unknown) } }
                .collect {}
        }
    }

    // Re-derives isReadOnly from the trip's live status instead of trusting the nav-arg snapshot
    // forever, so the input gating doesn't go stale if the trip completes or is cancelled
    // while this screen is already open.
    private fun observeTripStatus() {
        viewModelScope.launch {
            tripRepository.getTripByIdFlow(tripId)
                .catch { }
                .collect { trip ->
                    val status = trip?.status ?: return@collect
                    val readOnly = status is TripStatus.Completed || status is TripStatus.Cancelled
                    _state.update { it.copy(isReadOnly = readOnly) }
                }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnInputChange -> _state.update { it.copy(inputText = action.text) }
            is ChatAction.OnQuickReplyClick -> sendText(action.text)
            ChatAction.OnSendClick -> {
                val text = _state.value.inputText.trim()
                if (text.isNotBlank() && sendText(text)) _state.update { it.copy(inputText = "") }
            }
            is ChatAction.OnRetryMessage -> retry(action.messageId)
            ChatAction.OnBackClick -> viewModelScope.launch { _events.emit(ChatEvent.NavigateBack) }
            ChatAction.OnRetryLoad -> loadMessages()
        }
    }

    /** Shows the message at once as sending and delivers it; false when nobody is signed in. */
    private fun sendText(text: String): Boolean {
        val senderId = authRepository.getCurrentUserId() ?: return false
        val message = sendMessageUseCase.createMessage(bookingId, senderId, text, Clock.System.now().toEpochMilliseconds())
        _state.update { it.copy(outgoing = it.outgoing + ChatMessageItem(message, MessageStatus.Sending)) }
        deliver(message)
        return true
    }

    private fun retry(messageId: String) {
        val failed = _state.value.outgoing.firstOrNull {
            it.message.id == messageId && it.status == MessageStatus.Failed
        } ?: return
        setOutgoingStatus(messageId, MessageStatus.Sending)
        deliver(failed.message)
    }

    private fun deliver(message: Message) {
        viewModelScope.launch {
            sendMessageUseCase(message)
                .onSuccess {
                    // The stored copy, already in the listener's list, takes over.
                    _state.update { state -> state.copy(outgoing = state.outgoing.filterNot { it.message.id == message.id }) }
                }
                .onFailure { setOutgoingStatus(message.id, MessageStatus.Failed) }
        }
    }

    private fun setOutgoingStatus(messageId: String, status: MessageStatus) {
        _state.update { state ->
            state.copy(outgoing = state.outgoing.map { if (it.message.id == messageId) it.copy(status = status) else it })
        }
    }
}
