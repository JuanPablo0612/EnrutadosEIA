package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_step_done
import enrutadoseia.composeapp.generated.resources.check_24px
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.home_getting_started_progress
import enrutadoseia.composeapp.generated.resources.home_getting_started_title
import enrutadoseia.composeapp.generated.resources.home_how_it_works_1
import enrutadoseia.composeapp.generated.resources.home_how_it_works_2
import enrutadoseia.composeapp.generated.resources.home_how_it_works_3
import enrutadoseia.composeapp.generated.resources.home_how_it_works_title
import enrutadoseia.composeapp.generated.resources.home_step_account
import enrutadoseia.composeapp.generated.resources.home_step_book
import enrutadoseia.composeapp.generated.resources.home_step_book_hint
import enrutadoseia.composeapp.generated.resources.home_step_vehicle
import enrutadoseia.composeapp.generated.resources.home_step_vehicle_hint
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * A three-step checklist for new users: the account (always done by the time Inicio shows),
 * a first booking and a registered vehicle. Pending steps are tappable shortcuts.
 */
@Composable
fun GettingStartedCard(
    hasBookedBefore: Boolean,
    hasVehicles: Boolean,
    onSearch: () -> Unit,
    onRegisterVehicle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val total = 3
    val done = 1 + listOf(hasBookedBefore, hasVehicles).count { it }
    CarpoolListCard(modifier = modifier, contentPadding = PaddingValues(0.dp)) {
        Column(
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.home_getting_started_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                )
                Text(
                    text = stringResource(Res.string.home_getting_started_progress, done, total),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { done.toFloat() / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                drawStopIndicator = {},
                gapSize = 0.dp,
            )
        }
        ChecklistStep(title = stringResource(Res.string.home_step_account), isDone = true)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ChecklistStep(
            title = stringResource(Res.string.home_step_book),
            hint = stringResource(Res.string.home_step_book_hint),
            isDone = hasBookedBefore,
            onClick = onSearch,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ChecklistStep(
            title = stringResource(Res.string.home_step_vehicle),
            hint = stringResource(Res.string.home_step_vehicle_hint),
            isDone = hasVehicles,
            onClick = onRegisterVehicle,
        )
    }
}

@Composable
private fun ChecklistStep(
    title: String,
    isDone: Boolean,
    hint: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val doneDescription = stringResource(Res.string.cd_step_done, title)
    val rowModifier = if (!isDone && onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            .then(if (isDone) Modifier.clearAndSetSemantics { contentDescription = doneDescription } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        StepMarker(isDone = isDone)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isDone) TextDecoration.LineThrough else null,
            )
            if (!isDone && hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (!isDone && onClick != null) {
            Icon(
                imageVector = vectorResource(Res.drawable.chevron_right_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StepMarker(isDone: Boolean) {
    if (isDone) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.check_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
    }
}

/** A static primer for brand-new users; no data behind it. */
@Composable
fun HowItWorksSection(modifier: Modifier = Modifier) {
    val steps = listOf(
        stringResource(Res.string.home_how_it_works_1),
        stringResource(Res.string.home_how_it_works_2),
        stringResource(Res.string.home_how_it_works_3),
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.home_how_it_works_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        CarpoolListCard(contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.xs)) {
            steps.forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.padding(vertical = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(20.dp),
                    )
                    Text(text = step, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
