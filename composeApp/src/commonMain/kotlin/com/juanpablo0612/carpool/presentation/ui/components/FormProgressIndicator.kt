package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * A lightweight progress cue for long, single-scroll forms (route creation, vehicle
 * registration) that otherwise give no sense of how much is left — just a completed-sections
 * count, not a stepper (the form itself stays a single scroll, this isn't paginating it).
 */
@Composable
fun FormProgressIndicator(
    completedSections: Int,
    totalSections: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.screenHorizontal)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.xs))
        LinearProgressIndicator(
            progress = { if (totalSections == 0) 0f else completedSections.toFloat() / totalSections },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.sm))
    }
}

@Preview
@Composable
private fun FormProgressIndicatorPreview() {
    CarpoolTheme {
        FormProgressIndicator(
            completedSections = 2,
            totalSections = 4,
            label = "2 of 4 required fields completed",
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}
