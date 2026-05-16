package com.juanpablo0612.carpool.domain.trip.model

sealed class PickupStatus(val key: String) {
    data object Waiting : PickupStatus("WAITING")
    data object PickedUp : PickupStatus("PICKED_UP")
    data object DroppedOff : PickupStatus("DROPPED_OFF")

    companion object {
        fun fromKey(key: String): PickupStatus = when (key) {
            "PICKED_UP" -> PickedUp
            "DROPPED_OFF" -> DroppedOff
            else -> Waiting
        }
    }
}
