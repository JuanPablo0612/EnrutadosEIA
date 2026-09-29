package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.home.greetingResourceForTime
import com.juanpablo0612.carpool.presentation.home.homeHeaderDate
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.notifications_24px
import enrutadoseia.composeapp.generated.resources.notifications_title
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Instant

/** Today's date over a time-of-day greeting, with the notifications entry point beside it. */
@Composable
fun HomeHeader(
    firstName: String,
    now: Long,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val local = Instant.fromEpochMilliseconds(now).toLocalDateTime(TimeZone.currentSystemDefault())
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = homeHeaderDate(local.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(greetingResourceForTime(local.hour), firstName),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
        }
        IconButton(onClick = onOpenNotifications) {
            Icon(
                imageVector = vectorResource(Res.drawable.notifications_24px),
                contentDescription = stringResource(Res.string.notifications_title),
            )
        }
    }
}
