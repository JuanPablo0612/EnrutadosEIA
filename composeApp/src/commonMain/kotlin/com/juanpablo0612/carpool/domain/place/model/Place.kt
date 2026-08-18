package com.juanpablo0612.carpool.domain.place.model

data class Place(
    val id: String = "",
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: PlaceType = PlaceType.Other,
    val isCampusPreset: Boolean = false,
    val ownerId: String = "",
) {
    companion object {
        val EIA_LAS_PALMAS = Place(
            id = "eia_las_palmas",
            name = "EIA — Sede Las Palmas",
            address = "Km 2 + 200 Vía Aeropuerto JMC, Envigado",
            latitude = 6.1633,
            longitude = -75.4913,
            type = PlaceType.University,
            isCampusPreset = true,
        )
        val EIA_ZUNIGA = Place(
            id = "eia_zuniga",
            name = "EIA — Sede Zúñiga",
            address = "Cra 14 sur #6-150, Envigado",
            latitude = 6.1560,
            longitude = -75.5960,
            type = PlaceType.University,
            isCampusPreset = true,
        )
        val campusPresets = listOf(EIA_LAS_PALMAS, EIA_ZUNIGA)
        val UNIVERSITY_EIA = EIA_LAS_PALMAS
    }
}
