package com.juanpablo0612.carpool.data.vehicle.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.vehicle.datasource.VehicleRemoteDataSource
import com.juanpablo0612.carpool.data.vehicle.datasource.VehicleStorageDataSource
import com.juanpablo0612.carpool.data.vehicle.model.VehicleDto
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val remoteDataSource: VehicleRemoteDataSource,
    private val storageDataSource: VehicleStorageDataSource
) : VehicleRepository {

    override suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit> {
        return try {
            val vehicleId = remoteDataSource.newVehicleDocumentId()
            var photoUrl = ""
            if (photoBytes != null) {
                // Image compression (FileKit) is a transformation of the input, not an I/O call,
                // so it stays here rather than in a data source.
                val compressedBytes = FileKit.compressImage(
                    bytes = photoBytes,
                    quality = 80,
                    imageFormat = ImageFormat.JPEG
                )
                photoUrl = storageDataSource.uploadVehiclePhoto(vehicle.driverId, vehicleId, compressedBytes)
            }

            val existingCount = remoteDataSource.getUserVehicles(vehicle.driverId).first().size
            val isPrimary = existingCount == 0

            val dto = VehicleDto.fromDomain(vehicle).copy(
                id = vehicleId,
                photoUrl = photoUrl,
                isPrimary = isPrimary,
            )
            remoteDataSource.createVehicle(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.VehicleException.Unknown)
        }
    }

    override suspend fun getVehicleById(vehicleId: String): Result<Vehicle> {
        return try {
            val vehicle = remoteDataSource.getVehicleById(vehicleId).toDomain()
            Result.success(vehicle)
        } catch (_: Exception) {
            Result.failure(AppException.VehicleException.Unknown)
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit> {
        return try {
            var updatedVehicle = vehicle
            if (photoBytes != null) {
                // Image compression (FileKit) is a transformation of the input, not an I/O call,
                // so it stays here rather than in a data source.
                val compressedBytes = FileKit.compressImage(
                    bytes = photoBytes,
                    quality = 80,
                    imageFormat = ImageFormat.JPEG
                )
                val photoUrl = storageDataSource.uploadVehiclePhoto(vehicle.driverId, vehicle.id, compressedBytes)
                updatedVehicle = vehicle.copy(photoUrl = photoUrl)
            }
            val dto = VehicleDto.fromDomain(updatedVehicle)
            remoteDataSource.updateVehicle(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.VehicleException.Unknown)
        }
    }

    override suspend fun deleteVehicle(vehicleId: String, driverId: String): Result<Unit> {
        return try {
            remoteDataSource.deleteVehicle(vehicleId)
            try {
                storageDataSource.deleteVehiclePhoto(driverId, vehicleId)
            } catch (_: Exception) {
                /* ignore if photo not found */
            }
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.VehicleException.Unknown)
        }
    }

    override suspend fun setPrimaryVehicle(userId: String, vehicleId: String): Result<Unit> {
        return try {
            val vehicles = remoteDataSource.getUserVehicles(userId).first()
            remoteDataSource.setPrimaryVehicle(vehicles, vehicleId)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.VehicleException.Unknown)
        }
    }

    override fun getUserVehicles(userId: String): Flow<List<Vehicle>> {
        return remoteDataSource.getUserVehicles(userId)
            .map { list -> list.map { it.toDomain() } }
    }
}
