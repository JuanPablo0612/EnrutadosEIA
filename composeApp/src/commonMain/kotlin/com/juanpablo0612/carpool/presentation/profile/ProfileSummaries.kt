package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle

/** What the profile's vehicle card shows: the vehicle to feature and how many there are. */
data class VehicleSummary(val featured: Vehicle?, val count: Int)

/** What the profile's payment card shows: how many methods and which kinds, in picker order. */
data class PaymentSummary(val count: Int, val kinds: List<PaymentMethodType>)

/** The primary vehicle is the one trips default to, so it's the one worth featuring. */
fun List<Vehicle>.toVehicleSummary(): VehicleSummary =
    VehicleSummary(featured = firstOrNull { it.isPrimary } ?: firstOrNull(), count = size)

fun List<PaymentMethod>.toPaymentSummary(): PaymentSummary {
    val present = map { it.type }.toSet()
    return PaymentSummary(count = size, kinds = PaymentMethodType.all.filter { it in present })
}

/** "Mazda 3 gris · ABC 123": make, model and colour, then the plate. */
fun Vehicle.summaryLine(): String {
    val name = listOf(brand, model, color.lowercase()).filter { it.isNotBlank() }.joinToString(" ")
    return listOf(name, displayPlate(licensePlate)).filter { it.isNotBlank() }.joinToString(" · ")
}

private val PLATE = Regex("^([A-Z]{3})([0-9A-Z]{3})$")

/** Plates are stored as six bare characters; they read as "ABC 123", like on the car. */
fun displayPlate(plate: String): String =
    PLATE.matchEntire(plate)?.let { "${it.groupValues[1]} ${it.groupValues[2]}" } ?: plate
