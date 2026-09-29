package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private fun unused(): Nothing = error("not used by these tests")

class FakeAuthRepository(private val uid: String?) : AuthRepository {
    override fun getCurrentUserId(): String? = uid
    override fun getCurrentUserEmail(): String? = unused()
    override suspend fun login(email: String, password: String) = unused()
    override suspend fun register(email: String, password: String, name: String, phone: String, photoBytes: ByteArray?) = unused()
    override suspend fun sendEmailVerification() = unused()
    override suspend fun refreshEmailVerification() = unused()
    override suspend fun logout() = unused()
    override suspend fun sendPasswordResetEmail(email: String) = unused()
    override suspend fun getCurrentUser(): Result<User> = unused()
    override suspend fun getPublicProfile(userId: String): Result<PublicProfile> = unused()
    override suspend fun updateProfile(name: String, phone: String?, bio: String?, photoBytes: ByteArray?): Result<User> = unused()
    override suspend fun deleteAccount() = unused()
}

class FakeVehicleRepository(private val vehicles: List<Vehicle>) : VehicleRepository {
    override suspend fun getVehicleById(vehicleId: String): Result<Vehicle> =
        vehicles.firstOrNull { it.id == vehicleId }?.let { Result.success(it) } ?: Result.failure(Exception())
    override fun getUserVehicles(userId: String): Flow<List<Vehicle>> = flowOf(vehicles.filter { it.driverId == userId })
    override suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray?) = unused()
    override suspend fun updateVehicle(vehicle: Vehicle, photoBytes: ByteArray?) = unused()
    override suspend fun deleteVehicle(vehicleId: String, driverId: String) = unused()
    override suspend fun setPrimaryVehicle(userId: String, vehicleId: String) = unused()
}

class FakeRouteRepository : RouteRepository {
    val created = mutableListOf<Route>()
    override suspend fun createRoute(route: Route): Result<String> {
        created += route
        return Result.success("route${created.size}")
    }
    override fun getUserRoutes(userId: String): Flow<List<Route>> = unused()
    override suspend fun getRouteById(id: String): Result<Route> = unused()
    override suspend fun updateRoute(route: Route): Result<Unit> = unused()
    override suspend fun deleteRoute(id: String): Result<Unit> = unused()
}

class FakeTripRepository(
    private val existing: List<Trip> = emptyList(),
    private val failCreate: Boolean = false,
) : TripRepository {
    val created = mutableListOf<Trip>()
    override suspend fun createTrip(trip: Trip): Result<String> {
        if (failCreate) return Result.failure(Exception())
        created += trip
        return Result.success("trip${created.size}")
    }
    override suspend fun createTrips(trips: List<Trip>): Result<List<String>> {
        if (failCreate) return Result.failure(Exception())
        created += trips
        return Result.success(trips.indices.map { "trip$it" })
    }
    override fun getDriverTrips(driverId: String): Flow<List<Trip>> = flowOf(existing)
    override fun getAvailableTrips(): Flow<List<Trip>> = unused()
    override suspend fun getTripById(id: String): Result<Trip> = unused()
    override fun getTripByIdFlow(id: String): Flow<Trip?> = unused()
    override suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit> = unused()
    override suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double): Result<Unit> = unused()
    override suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String): Result<Unit> = unused()
}

fun vehicle(id: String = "v1", driverId: String = "d1", seats: Int = 4) = Vehicle(
    id = id,
    driverId = driverId,
    brand = "Mazda",
    model = "3",
    licensePlate = "ABC123",
    color = "Gris",
    year = 2020,
    seatsAvailable = seats,
)
