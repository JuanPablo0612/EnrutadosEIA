package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable

/**
 * Whether this device can take a photo. The iOS Simulator and Macs running the iPad app can't, and
 * the camera picker crashes there instead of failing, so screens hide the option.
 */
@Composable
expect fun rememberIsCameraAvailable(): Boolean
