package com.juanpablo0612.carpool.domain.places.model

data class Place(
    val id: String = "",
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val UNIVERSITY_EIA = Place(
            id = "university_eia",
            name = "Universidad EIA - Sede Las Palmas",
            address = "Km 2 + 200 Vía Aeropuerto JMC, Envigado, Antioquia",
            latitude = 6.1633,
            longitude = -75.4913
        )
    }
}
