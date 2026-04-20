package com.juanpablo0612.carpool.data.vehicles.repository

import com.juanpablo0612.carpool.data.vehicles.model.VehicleDto
import com.juanpablo0612.carpool.data.vehicles.upload
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : VehicleRepository {

    override suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val vehicleId = docRef.id

            val photoRef = storage.reference.child("$STORAGE_PATH/${vehicle.driverId}/$vehicleId.jpg")
            photoRef.upload(photoBytes)
            val photoUrl = photoRef.getDownloadUrl()

            val dto = VehicleDto.fromDomain(vehicle).copy(id = vehicleId, photoUrl = photoUrl)
            docRef.set(VehicleDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fetches all vehicles and filters by driverId client-side.
    // Suitable for small per-driver datasets (typically 1–3 vehicles).
    override fun getUserVehicles(userId: String): Flow<List<Vehicle>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(VehicleDto.serializer()).toDomain() }
                    .filter { it.driverId == userId }
            }
    }

    companion object {
        private const val COLLECTION_NAME = "vehicles"
        private const val STORAGE_PATH = "vehicles"
    }
}
