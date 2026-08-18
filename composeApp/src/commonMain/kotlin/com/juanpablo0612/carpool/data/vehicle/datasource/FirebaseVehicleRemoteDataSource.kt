package com.juanpablo0612.carpool.data.vehicle.datasource

import com.juanpablo0612.carpool.data.vehicle.model.VehicleDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseVehicleRemoteDataSource(
    private val firestore: FirebaseFirestore
) : VehicleRemoteDataSource {

    override fun newVehicleDocumentId(): String {
        return firestore.collection(COLLECTION_NAME).document.id
    }

    override suspend fun createVehicle(vehicle: VehicleDto) {
        firestore.collection(COLLECTION_NAME).document(vehicle.id).set(VehicleDto.serializer(), vehicle)
    }

    override suspend fun getVehicleById(vehicleId: String): VehicleDto {
        val doc = firestore.collection(COLLECTION_NAME).document(vehicleId).get()
        return doc.data(VehicleDto.serializer())
    }

    override suspend fun updateVehicle(vehicle: VehicleDto) {
        firestore.collection(COLLECTION_NAME).document(vehicle.id).set(VehicleDto.serializer(), vehicle)
    }

    override suspend fun deleteVehicle(vehicleId: String) {
        firestore.collection(COLLECTION_NAME).document(vehicleId).delete()
    }

    override suspend fun setPrimaryVehicle(vehicles: List<VehicleDto>, vehicleId: String) {
        val batch = firestore.batch()
        vehicles.forEach { v ->
            val ref = firestore.collection(COLLECTION_NAME).document(v.id)
            batch.update(ref, mapOf("isPrimary" to (v.id == vehicleId)))
        }
        batch.commit()
    }

    override fun getUserVehicles(userId: String): Flow<List<VehicleDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(VehicleDto.serializer()) }
            }
    }

    companion object {
        private const val COLLECTION_NAME = "vehicles"
    }
}
