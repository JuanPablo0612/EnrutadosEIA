package com.juanpablo0612.carpool.domain.trip.model

import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle

/**
 * Who drives, as they were when the trip was published. Stored on the trip so browsing and
 * matching trips needs no read of the driver's profile; the live profile (with the rating) is
 * read only when someone opens it.
 */
data class TripDriver(
    val name: String = "",
    val photoUrl: String? = null,
) {
    companion object {
        fun from(user: User) = TripDriver(name = user.name.orEmpty(), photoUrl = user.photoUrl)
    }
}

/**
 * The car a trip runs in, as it was when the trip was published, for the same reason as
 * [TripDriver]. The plate is deliberately left out: it identifies the car on the street, so it is
 * shown only to confirmed passengers, from the vehicle document.
 */
data class TripVehicle(
    val brand: String = "",
    val model: String = "",
    val color: String = "",
) {
    companion object {
        fun from(vehicle: Vehicle) = TripVehicle(brand = vehicle.brand, model = vehicle.model, color = vehicle.color)
    }
}
