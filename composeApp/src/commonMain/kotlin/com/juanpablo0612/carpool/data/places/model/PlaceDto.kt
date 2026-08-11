package com.juanpablo0612.carpool.data.places.model

import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.model.PlaceType
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDto(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = "OTHER",
    val isCampusPreset: Boolean = false,
    val ownerId: String = "",
) {
    fun toDomain() = Place(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        type = when (type) {
            "HOME" -> PlaceType.Home
            "WORK" -> PlaceType.Work
            "GYM" -> PlaceType.Gym
            "UNIVERSITY" -> PlaceType.University
            else -> PlaceType.Other
        },
        isCampusPreset = isCampusPreset,
        ownerId = ownerId,
    )

    companion object {
        fun fromDomain(p: Place) = PlaceDto(
            id = p.id,
            name = p.name,
            address = p.address,
            latitude = p.latitude,
            longitude = p.longitude,
            type = when (p.type) {
                is PlaceType.Home -> "HOME"
                is PlaceType.Work -> "WORK"
                is PlaceType.Gym -> "GYM"
                is PlaceType.University -> "UNIVERSITY"
                else -> "OTHER"
            },
            isCampusPreset = p.isCampusPreset,
            ownerId = p.ownerId,
        )
    }
}
