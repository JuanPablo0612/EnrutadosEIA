package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.home_pending_banner
import enrutadoseia.composeapp.generated.resources.home_pending_banner_subtitle
import enrutadoseia.composeapp.generated.resources.person_add_24px
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * "2 personas quieren un cupo": a count and a way in. Inicio deliberately shows no requester
 * details, so it needs no reads beyond the pending-request query; the requests screen loads them.
 */
@Composable
fun PendingRequestsBanner(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extended = LocalExtendedColors.current
    CarpoolListCard(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = extended.warningContainer,
            contentColor = extended.onWarningContainer,
        ),
        border = BorderStroke(1.dp, extended.warning.copy(alpha = 0.3f)),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.person_add_24px),
                    contentDescription = null,
                    tint = extended.warning,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pluralStringResource(Res.plurals.home_pending_banner, count, count),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(Res.string.home_pending_banner_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Icon(imageVector = vectorResource(Res.drawable.chevron_right_24px), contentDescription = null)
        }
    }
}
