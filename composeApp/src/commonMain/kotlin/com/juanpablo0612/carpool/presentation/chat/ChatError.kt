package com.juanpablo0612.carpool.presentation.chat

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class ChatError {
    data object Unknown : ChatError()

    fun asStringResource(): StringResource = when (this) {
        Unknown -> Res.string.error_unknown
    }
}
