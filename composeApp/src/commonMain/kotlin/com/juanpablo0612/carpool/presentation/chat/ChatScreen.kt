package com.juanpablo0612.carpool.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.chat.components.ChatInputRow
import com.juanpablo0612.carpool.presentation.chat.components.MessageBubble
import com.juanpablo0612.carpool.presentation.chat.components.QuickRepliesRow
import com.juanpablo0612.carpool.presentation.chat.components.ReadOnlyBanner
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatEvent.NavigateBack -> onBackClick()
        }
    }

    ChatContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    state: ChatUiState,
    onAction: (ChatAction) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = state.otherPartyName.ifBlank { "Chat" },
                onBack = { onAction(ChatAction.OnBackClick) },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            if (state.isReadOnly) {
                ReadOnlyBanner()
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(state.messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            isOwn = message.senderId == state.currentUserId
                        )
                    }
                }
            }

            if (!state.isReadOnly) {
                QuickRepliesRow(onQuickReply = { onAction(ChatAction.OnQuickReplyClick(it)) })
                ChatInputRow(
                    text = state.inputText,
                    isSending = state.isSending,
                    onTextChange = { onAction(ChatAction.OnInputChange(it)) },
                    onSendClick = { onAction(ChatAction.OnSendClick) }
                )
            }
        }
    }
}
