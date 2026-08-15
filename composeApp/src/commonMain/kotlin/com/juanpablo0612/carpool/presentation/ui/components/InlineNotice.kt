package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.check_circle_24px
import org.jetbrains.compose.resources.vectorResource

/**
 * The non-error counterpart to [ErrorMessage]. AGENTS.md §16 forbids SnackBars for errors, but
 * left success/info feedback with no real substitute — mutations that succeed today are
 * confirmed only by `popBackStack()`, indistinguishable from pressing back or a silent failure.
 * `BookingRequestsScreen`'s private `TripFilledBanner` already demonstrated the right shape (a
 * real, non-transient, dismiss-on-tap banner on a paired container/onContainer colour role);
 * this generalises that pattern — including tap-to-dismiss rather than a separate icon button,
 * since the project has no close/X glyph among its local vectors (see note below) — for reuse.
 *
 * Screens are expected to wire this in a later wave; it intentionally isn't called from any
 * screen yet.
 */
enum class InlineNoticeKind {
    Success,
    Info,
}

@Composable
fun InlineNotice(
    message: String,
    kind: InlineNoticeKind,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    val (containerColor, contentColor) = when (kind) {
        // Mirrors TripFilledBanner's existing primaryContainer/onPrimaryContainer pairing.
        InlineNoticeKind.Success -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        // A distinct semantic slot from Success, still a real M3 container/onContainer pair.
        InlineNoticeKind.Info -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    val dismissModifier = if (onDismiss != null) {
        Modifier.clickable(role = Role.Button, onClick = onDismiss)
    } else {
        Modifier
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth().then(dismissModifier)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info has no leading icon: the project's local vector set (composeResources/drawable)
            // has no rounded "info" glyph. Success reuses check_circle_24px, already used
            // elsewhere for the same affirmative meaning.
            if (kind == InlineNoticeKind.Success) {
                Icon(
                    imageVector = vectorResource(Res.drawable.check_circle_24px),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
            }
            Text(
                text = message,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun InlineNoticeSuccessPreview() {
    CarpoolTheme {
        InlineNotice(
            message = "Your trip was published successfully.",
            kind = InlineNoticeKind.Success,
            onDismiss = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@Preview
@Composable
private fun InlineNoticeInfoPreview() {
    CarpoolTheme {
        InlineNotice(
            message = "Your driver will arrive in about 10 minutes.",
            kind = InlineNoticeKind.Info,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
