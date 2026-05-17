package com.juanpablo0612.carpool.data.places.service

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.mobile
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.mobile
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile

internal actual fun createGeolocator(): Geolocator = Geolocator.mobile()
internal actual fun createGeocoder(): Geocoder = Geocoder.mobile()
internal actual fun createAutocomplete(): Autocomplete<Place> = Autocomplete.mobile()
