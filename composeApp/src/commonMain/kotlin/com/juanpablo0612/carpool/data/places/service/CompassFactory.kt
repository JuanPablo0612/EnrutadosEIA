package com.juanpablo0612.carpool.data.places.service

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator

internal expect fun createGeolocator(): Geolocator
internal expect fun createGeocoder(): Geocoder
internal expect fun createAutocomplete(): Autocomplete<Place>
