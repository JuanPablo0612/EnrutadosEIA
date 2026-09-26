package com.juanpablo0612.carpool.presentation.mytrips

import kotlinx.serialization.Serializable

/**
 * The two halves of "Mis viajes". An enum rather than a sealed class because it travels as a
 * type-safe navigation argument (`Route.MyTrips`), which supports enums natively.
 */
@Serializable
enum class MyTripsTab { Passenger, Driver }
