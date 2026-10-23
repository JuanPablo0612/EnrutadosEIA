package com.juanpablo0612.carpool.presentation.notification

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notifications_unread_count
import org.jetbrains.compose.resources.pluralStringResource

/** Counts above this read as "9+", so the badge stays a small pill however many pile up. */
internal const val MAX_BADGE_COUNT = 9

/** The text inside an unread badge, or null when there is nothing unread and no badge shows. */
internal fun unreadBadgeLabel(count: Int): String? = when {
    count <= 0 -> null
    count > MAX_BADGE_COUNT -> "$MAX_BADGE_COUNT+"
    else -> count.toString()
}

/**
 * Wraps [content] (an icon) with the unread count, or a plain dot when [showCount] is false. The
 * badge reads as a sentence ("3 unread notifications") rather than a bare number.
 */
@Composable
internal fun UnreadBadgedBox(
    count: Int,
    showCount: Boolean = true,
    content: @Composable () -> Unit,
) {
    val label = unreadBadgeLabel(count)
    BadgedBox(
        badge = {
            if (label != null) {
                val description = pluralStringResource(Res.plurals.notifications_unread_count, count, count)
                Badge(modifier = Modifier.clearAndSetSemantics { contentDescription = description }) {
                    if (showCount) Text(label)
                }
            }
        },
    ) {
        content()
    }
}
