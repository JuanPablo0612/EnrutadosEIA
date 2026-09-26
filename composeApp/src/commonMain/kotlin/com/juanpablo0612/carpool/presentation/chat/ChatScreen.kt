package com.juanpablo0612.carpool.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.chat.components.ChatInputRow
import com.juanpablo0612.carpool.presentation.chat.components.MessageBubble
import com.juanpablo0612.carpool.presentation.chat.components.QuickRepliesRow
import com.juanpablo0612.carpool.presentation.chat.components.ReadOnlyBanner
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.formatNumericDate
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chat_date_today
import enrutadoseia.composeapp.generated.resources.chat_date_yesterday
import enrutadoseia.composeapp.generated.resources.chat_default_title
import enrutadoseia.composeapp.generated.resources.chat_empty_description
import enrutadoseia.composeapp.generated.resources.chat_empty_title
import enrutadoseia.composeapp.generated.resources.chat_send_failed
import enrutadoseia.composeapp.generated.resources.mail_24px
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                        .clickable { onAction(ChatAction.OnDismissSendError) },
                )
            }

            val timeZone = TimeZone.currentSystemDefault()
            val messagesWithDateFlag = remember(state.messages) {
                var lastDate: LocalDate? = null
                state.messages.map { message ->
                    val date = Instant.fromEpochMilliseconds(message.timestamp)
                        .toLocalDateTime(timeZone).date
                    val isNewDay = date != lastDate
                    lastDate = date
                    message to isNewDay
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
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
                    else -> items(messagesWithDateFlag, key = { it.first.id }) { (message, isNewDay) ->
                        if (isNewDay) {
                            DateSeparator(epochMs = message.timestamp)
                        }
                        MessageBubble(
                            message = message,
                            isOwn = message.senderId == state.currentUserId
                        )
                    }
                }
            }

            if (!state.isReadOnly) {
                QuickRepliesRow(
                    onQuickReply = { onAction(ChatAction.OnQuickReplyClick(it)) },
                    enabled = !state.isSending,
                )
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

// Message timestamps were time-of-day only, with no day context — a multi-day conversation
// looked ambiguously ordered when scrolled back through. This mirrors the day-divider pattern
// common to chat UIs, distinct from RelativeDateGroup (which is future-oriented: Today/Tomorrow/
// This week/Later) since chat history is always in the past.
@Composable
private fun DateSeparator(epochMs: Long, modifier: Modifier = Modifier) {
    val timeZone = TimeZone.currentSystemDefault()
    val date = remember(epochMs) { Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(timeZone).date }
    val today = remember { Clock.System.now().toLocalDateTime(timeZone).date }
    val label = when {
        date == today -> stringResource(Res.string.chat_date_today)
        date == LocalDate.fromEpochDays(today.toEpochDays() - 1) -> stringResource(Res.string.chat_date_yesterday)
        else -> formatNumericDate(date)
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.sm)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
