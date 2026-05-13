package com.juanpablo0612.carpool.data.places.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.juanpablo0612.carpool.domain.places.model.Coordinates
import com.juanpablo0612.carpool.domain.places.service.LocationService
import kotlinx.coroutines.tasks.await

class AndroidLocationService(private val context: Context) : LocationService {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentCoordinates(): Coordinates? {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return null

        val cts = CancellationTokenSource()
        return try {
            val location = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
            location?.let { Coordinates(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
