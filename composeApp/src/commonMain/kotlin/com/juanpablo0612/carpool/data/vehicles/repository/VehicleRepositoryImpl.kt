package com.juanpablo0612.carpool.data.vehicles.repository

import com.juanpablo0612.carpool.data.vehicles.model.VehicleDto
import com.juanpablo0612.carpool.data.vehicles.upload
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : VehicleRepository {

    override suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val vehicleId = docRef.id
            val compressedBytes = FileKit.compressImage(
                bytes = photoBytes,
                quality = 80,
                imageFormat = ImageFormat.JPEG
            )

            val photoRef = storage.reference.child("$STORAGE_PATH/${vehicle.driverId}/$vehicleId.jpg")
            photoRef.upload(compressedBytes)
            val photoUrl = photoRef.getDownloadUrl()

            val existingCount = getUserVehicles(vehicle.driverId).first().size
            val isPrimary = existingCount == 0

            val dto = VehicleDto.fromDomain(vehicle).copy(
                id = vehicleId,
                photoUrl = photoUrl,
                isPrimary = isPrimary,
            )
            docRef.set(VehicleDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVehicleById(vehicleId: String): Result<Vehicle> {
        return try {
            val doc = firestore.collection(COLLECTION_NAME).document(vehicleId).get()
            val vehicle = doc.data(VehicleDto.serializer()).toDomain()
            Result.success(vehicle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit> {
        return try {
            var updatedVehicle = vehicle
            if (photoBytes != null) {
                val compressedBytes = FileKit.compressImage(
                    bytes = photoBytes,
                    quality = 80,
                    imageFormat = ImageFormat.JPEG
                )
                val photoRef = storage.reference.child("$STORAGE_PATH/${vehicle.driverId}/${vehicle.id}.jpg")
                photoRef.upload(compressedBytes)
                val photoUrl = photoRef.getDownloadUrl()
                updatedVehicle = vehicle.copy(photoUrl = photoUrl)
            }
            val dto = VehicleDto.fromDomain(updatedVehicle)
            firestore.collection(COLLECTION_NAME).document(vehicle.id).set(VehicleDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVehicle(vehicleId: String, driverId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(vehicleId).delete()
            val photoRef = storage.reference.child("$STORAGE_PATH/$driverId/$vehicleId.jpg")
            try { photoRef.delete() } catch (_: Exception) { /* ignore if photo not found */ }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setPrimaryVehicle(userId: String, vehicleId: String): Result<Unit> {
        return try {
            val vehicles = getUserVehicles(userId).first()
            val batch = firestore.batch()
            vehicles.forEach { v ->
                val ref = firestore.collection(COLLECTION_NAME).document(v.id)
                batch.update(ref, mapOf("isPrimary" to (v.id == vehicleId)))
            }
            batch.commit()
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

    override fun getDriverVehicles(driverId: String): Flow<List<Vehicle>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(VehicleDto.serializer()).toDomain() }
                    .filter { it.driverId == driverId }
            }
    }

    companion object {
        private const val COLLECTION_NAME = "vehicles"
        private const val STORAGE_PATH = "vehicles"
    }
}
