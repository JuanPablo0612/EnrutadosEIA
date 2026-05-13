package com.juanpablo0612.carpool.data.places.service

import com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.places.model.Coordinates
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService

class IosPlacesSearchService : PlacesSearchService {
    override suspend fun search(query: String): List<AutocompleteSuggestion> = emptyList()
    override suspend fun getCoordinates(placeId: String): Coordinates? = null
    override suspend fun reverseGeocode(coordinates: Coordinates): String? = null
}
