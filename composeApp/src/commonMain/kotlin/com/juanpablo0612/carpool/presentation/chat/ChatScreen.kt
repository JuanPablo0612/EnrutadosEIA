package com.juanpablo0612.carpool.presentation.chat

import androidx.compose.foundation.clickable
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
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chat_default_title
import enrutadoseia.composeapp.generated.resources.chat_empty_description
import enrutadoseia.composeapp.generated.resources.chat_empty_title
import enrutadoseia.composeapp.generated.resources.chat_send_failed
import enrutadoseia.composeapp.generated.resources.mail_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

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
                title = state.otherPartyName.ifBlank { stringResource(Res.string.chat_default_title) },
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

            if (state.sendFailed) {
                ErrorMessage(
                    message = stringResource(Res.string.chat_send_failed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onAction(ChatAction.OnDismissSendError) },
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    state.isLoading -> item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    state.error != null -> item {
                        ErrorState(
                            description = stringResource(state.error.asStringResource()),
                            onRetry = { onAction(ChatAction.OnRetryLoad) },
                            modifier = Modifier.fillParentMaxSize(),
                        )
                    }
                    state.messages.isEmpty() -> item {
                        EmptyState(
                            icon = vectorResource(Res.drawable.mail_24px),
                            title = stringResource(Res.string.chat_empty_title),
                            description = stringResource(Res.string.chat_empty_description),
                            modifier = Modifier.fillParentMaxSize(),
                        )
                    }
                    else -> items(state.messages, key = { it.id }) { message ->
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
