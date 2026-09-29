package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_contribution_free
import org.jetbrains.compose.resources.stringResource

/** Digits grouped with a dot, the Colombian thousands separator: 4000 → "4.000". */
fun groupThousands(amount: Int): String =
    amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** An amount in Colombian pesos: 4000 → "$4.000". */
fun formatPesos(amount: Int): String = "$" + groupThousands(amount)

/** A trip's contribution per passenger, or "Gratis" when there is none. */
@Composable
fun contributionLabel(amount: Int?): String =
    amount?.takeIf { it > 0 }?.let(::formatPesos) ?: stringResource(Res.string.trip_contribution_free)
