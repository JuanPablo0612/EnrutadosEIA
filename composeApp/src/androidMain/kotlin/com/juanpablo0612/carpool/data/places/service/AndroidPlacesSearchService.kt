package com.juanpablo0612.carpool.data.places.service

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.places.model.Coordinates
import com.juanpablo0612.carpool.domain.places.service.PlacesSearchService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import com.google.android.libraries.places.api.model.Place as GmsPlace

class AndroidPlacesSearchService(private val context: Context) : PlacesSearchService {
    private val client by lazy { Places.createClient(context) }

    override suspend fun search(query: String): List<AutocompleteSuggestion> {
        if (query.isBlank()) return emptyList()
        return try {
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setCountries("CO")
                .setSessionToken(token)
                .setQuery(query)
                .build()
            val response = client.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map { prediction ->
                AutocompleteSuggestion(
                    placeId = prediction.placeId,
                    primaryText = prediction.getPrimaryText(null).toString(),
                    secondaryText = prediction.getSecondaryText(null).toString(),
                    fullAddress = prediction.getFullText(null).toString(),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCoordinates(placeId: String): Coordinates? {
        return try {
            val fields = listOf(GmsPlace.Field.LOCATION)
            val request = FetchPlaceRequest.newInstance(placeId, fields)
            val response = client.fetchPlace(request).await()
            response.place.location?.let { Coordinates(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun reverseGeocode(coordinates: Coordinates): String? {
        return try {
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1) { addresses ->
                        cont.resume(addresses.firstOrNull()?.getAddressLine(0))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
            }
        } catch (e: Exception) {
            null
        }
    }
}
