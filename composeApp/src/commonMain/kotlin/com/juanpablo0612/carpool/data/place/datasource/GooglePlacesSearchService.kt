package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.data.place.model.AutocompleteRequestDto
import com.juanpablo0612.carpool.data.place.model.AutocompleteResponseDto
import com.juanpablo0612.carpool.data.place.model.GeocodingResponseDto
import com.juanpablo0612.carpool.data.place.model.PlaceDetailsResponseDto
import com.juanpablo0612.carpool.data.place.model.toAddress
import com.juanpablo0612.carpool.data.place.model.toCoordinates
import com.juanpablo0612.carpool.data.place.model.toDomain
import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val AUTOCOMPLETE_URL = "https://places.googleapis.com/v1/places:autocomplete"
private const val PLACE_DETAILS_URL = "https://places.googleapis.com/v1/places/"
private const val GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json"
private const val REGION_CODE = "CO"

class GooglePlacesSearchService(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : PlacesSearchService {

    override suspend fun search(query: String, sessionToken: String): List<AutocompleteSuggestion> {
        if (query.isBlank()) return emptyList()
        return try {
            val request = AutocompleteRequestDto(
                input = query,
                includedRegionCodes = listOf(REGION_CODE),
                sessionToken = sessionToken,
            )
            val response: AutocompleteResponseDto = httpClient.post(AUTOCOMPLETE_URL) {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", apiKey)
                setBody(request)
            }.body()

            response.suggestions.mapNotNull { it.placePrediction?.toDomain() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun resolvePlace(placeId: String, sessionToken: String): Coordinates? {
        return try {
            val response: PlaceDetailsResponseDto = httpClient.get("$PLACE_DETAILS_URL$placeId") {
                header("X-Goog-Api-Key", apiKey)
                header("X-Goog-FieldMask", "location")
                parameter("sessionToken", sessionToken)
            }.body()

            response.toCoordinates()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getPlaceAddress(placeId: String): String? {
        return try {
            val response: PlaceDetailsResponseDto = httpClient.get("$PLACE_DETAILS_URL$placeId") {
                header("X-Goog-Api-Key", apiKey)
                header("X-Goog-FieldMask", "formattedAddress")
            }.body()

            response.formattedAddress.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun reverseGeocode(coordinates: Coordinates): String? {
        return try {
            val response: GeocodingResponseDto = httpClient.get(GEOCODING_URL) {
                parameter("latlng", "${coordinates.latitude},${coordinates.longitude}")
                parameter("key", apiKey)
                parameter("result_type", "street_address|route|sublocality")
            }.body()

            response.results.firstOrNull()?.toAddress()
        } catch (_: Exception) {
            null
        }
    }
}
