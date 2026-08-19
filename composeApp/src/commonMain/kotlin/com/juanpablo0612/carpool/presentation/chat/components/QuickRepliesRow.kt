package com.juanpablo0612.carpool.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chat_quick_reply_en_camino
import enrutadoseia.composeapp.generated.resources.chat_quick_reply_esperando
import enrutadoseia.composeapp.generated.resources.chat_quick_reply_llegue
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuickRepliesRow(onQuickReply: (String) -> Unit) {
    val enCamino = stringResource(Res.string.chat_quick_reply_en_camino)
    val llegue = stringResource(Res.string.chat_quick_reply_llegue)
    val esperando = stringResource(Res.string.chat_quick_reply_esperando)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listOf(enCamino, llegue, esperando)) { reply ->
            SuggestionChip(
                onClick = { onQuickReply(reply) },
                label = { Text(reply) }
            )
        }
    }
}
