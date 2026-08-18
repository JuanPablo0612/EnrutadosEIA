package com.juanpablo0612.carpool.data.vehicle.datasource

import dev.gitlive.firebase.storage.FirebaseStorage

class FirebaseVehicleStorageDataSource(
    private val storage: FirebaseStorage
) : VehicleStorageDataSource {

    override suspend fun uploadVehiclePhoto(driverId: String, vehicleId: String, bytes: ByteArray): String {
        val photoRef = storage.reference.child("$STORAGE_PATH/$driverId/$vehicleId.jpg")
        photoRef.upload(bytes)
        return photoRef.getDownloadUrl()
    }

    override suspend fun deleteVehiclePhoto(driverId: String, vehicleId: String) {
        storage.reference.child("$STORAGE_PATH/$driverId/$vehicleId.jpg").delete()
    }

    companion object {
        private const val STORAGE_PATH = "vehicles"
    }
}
