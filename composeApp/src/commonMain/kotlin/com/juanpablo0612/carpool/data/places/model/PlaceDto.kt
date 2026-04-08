package com.juanpablo0612.carpool.data.places.model

import com.juanpablo0612.carpool.domain.places.model.Place
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDto(
    val id: String = "",
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain(): Place = Place(id, name, address, latitude, longitude)
    
    companion object {
        fun fromDomain(place: Place): PlaceDto = PlaceDto(
            id = place.id,
            name = place.name,
            address = place.address,
            latitude = place.latitude,
            longitude = place.longitude
        )
    }
}
