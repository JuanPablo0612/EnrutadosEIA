package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MatchTripsUseCaseTest {

    private val useCase = MatchTripsUseCase()

    private fun place(name: String, lat: Double, lng: Double) =
        Place(name = name, address = "", latitude = lat, longitude = lng)

    // 0.009 degrees of latitude is roughly 1 km.
    private val envigado = place("Envigado", 6.170, -75.585)
    private val waypoint = place("Parada", 6.168, -75.560)
    private val campus = Place.EIA_LAS_PALMAS

    private fun trip(
        id: String = "t1",
        origin: Place = envigado,
        destination: Place = campus,
        waypoints: List<Place> = listOf(waypoint),
        departure: Long = 1_000_000L,
        seats: Int = 3,
        confirmed: Int = 0,
        contribution: Int? = null,
    ) = Trip(
        id = id,
        routeId = "",
        driverId = "d",
        vehicleId = "v",
        origin = origin,
        destination = destination,
        waypoints = waypoints,
        departureTime = departure,
        seatCount = seats,
        contributionPerPassenger = contribution,
        status = TripStatus.Active,
        confirmedSeats = confirmed,
    )

    private fun criteria(origin: Place? = null, destination: Place? = null) =
        TripSearchCriteria(origin = origin, destination = destination)

    @Test
    fun originNearTripOriginAndCampusDestinationMatches() {
        val near = place("Casa", 6.1745, -75.585)
        val match = useCase(listOf(trip()), criteria(near, campus)).single()
        assertEquals(0, match.pickup!!.pathIndex)
        assertEquals(2, match.dropoff!!.pathIndex)
        assertEquals(0.0, match.dropoff!!.distanceMeters)
    }

    @Test
    fun originNearWaypointPicksTheWaypoint() {
        val near = place("Casa", 6.1685, -75.561)
        val match = useCase(listOf(trip()), criteria(near, campus)).single()
        assertEquals(1, match.pickup!!.pathIndex)
        assertEquals("Parada", match.pickup!!.place.name)
    }

    @Test
    fun radiusIsRespected() {
        val far = place("Lejos", 6.1835, -75.585)
        assertTrue(useCase(listOf(trip()), criteria(far, campus)).isEmpty())
        val wider = criteria(far, campus).copy(maxWalkMeters = 2_000)
        assertEquals(1, useCase(listOf(trip()), wider).size)
    }

    @Test
    fun oppositeDirectionDoesNotMatch() {
        assertTrue(useCase(listOf(trip()), criteria(campus, envigado)).isEmpty())
    }

    @Test
    fun pickupMustComeBeforeDropoffOnThePath() {
        assertTrue(useCase(listOf(trip()), criteria(campus, waypoint)).isEmpty())
    }

    @Test
    fun cannotBoardAtFinalDestinationNorAlightAtOrigin() {
        assertTrue(useCase(listOf(trip()), criteria(origin = campus)).isEmpty())
        assertTrue(useCase(listOf(trip()), criteria(destination = envigado)).isEmpty())
    }

    @Test
    fun choosesTheClosestPickup() {
        val nearWaypoint = place("Casa", 6.1685, -75.5625)
        val match = useCase(listOf(trip()), criteria(nearWaypoint, campus).copy(maxWalkMeters = 3_000)).single()
        assertEquals(1, match.pickup!!.pathIndex)
    }

    @Test
    fun destinationOnlyMatchesTripsReachingCampusButNotLeavingIt() {
        val toCampus = trip(id = "to")
        val fromCampus = trip(id = "from", origin = campus, destination = envigado, waypoints = emptyList())
        val matches = useCase(listOf(toCampus, fromCampus), criteria(destination = campus))
        assertEquals(listOf("to"), matches.map { it.trip.id })
        assertNull(matches.single().pickup)
    }

    @Test
    fun originOnlyMatchesWithoutDropoff() {
        val match = useCase(listOf(trip()), criteria(origin = envigado)).single()
        assertNull(match.dropoff)
    }

    @Test
    fun noPlacesMatchesEverythingSortedByDeparture() {
        val trips = listOf(trip(id = "b", departure = 2_000L), trip(id = "a", departure = 1_000L))
        assertEquals(listOf("a", "b"), useCase(trips, criteria()).map { it.trip.id })
    }

    @Test
    fun timeWindowIsInclusive() {
        val window = criteria().copy(departureAroundEpochMs = 1_000_000L, toleranceMinutes = 30)
        val edge = trip(id = "edge", departure = 1_000_000L + 30 * 60_000L)
        val outside = trip(id = "out", departure = 1_000_000L + 30 * 60_000L + 1)
        assertEquals(listOf("edge"), useCase(listOf(edge, outside), window).map { it.trip.id })
    }

    @Test
    fun contributionFilterTreatsNullAsFree() {
        val cheap = criteria().copy(maxContribution = 3_000)
        val trips = listOf(trip(id = "free"), trip(id = "pricey", contribution = 5_000))
        assertEquals(listOf("free"), useCase(trips, cheap).map { it.trip.id })
    }

    @Test
    fun fullTripsAreExcludedAndSeatsComputed() {
        val trips = listOf(
            trip(id = "full", seats = 2, confirmed = 2),
            trip(id = "over", seats = 2, confirmed = 3),
            trip(id = "open", seats = 3, confirmed = 1),
        )
        val match = useCase(trips, criteria()).single()
        assertEquals("open", match.trip.id)
        assertEquals(2, match.availableSeats)
    }

    @Test
    fun stopsWithoutCoordinatesAreSkipped() {
        val noCoordinates = place("Sin coordenadas", 0.0, 0.0)
        val t = trip(waypoints = listOf(noCoordinates))
        assertEquals(0, useCase(listOf(t), criteria(envigado, campus)).single().pickup!!.pathIndex)
        assertTrue(useCase(listOf(t), criteria(noCoordinates, campus)).isEmpty())
    }

    @Test
    fun shorterWalkOutranksEarlierDeparture() {
        val home = place("Casa", 6.1709, -75.585)
        val nearTrip = trip(id = "near", departure = 2_000L)
        val farTrip = trip(id = "far", origin = place("Otro", 6.178, -75.585), waypoints = emptyList(), departure = 1_000L)
        val ids = useCase(listOf(farTrip, nearTrip), criteria(home, campus)).map { it.trip.id }
        assertEquals(listOf("near", "far"), ids)
    }

    @Test
    fun sameBucketIsOrderedByClosenessToChosenTime() {
        val early = trip(id = "early", departure = 1_000_000L)
        val late = trip(id = "late", departure = 1_600_000L)
        val aroundLate = criteria(envigado, campus).copy(departureAroundEpochMs = 1_500_000L)
        assertEquals(listOf("late", "early"), useCase(listOf(early, late), aroundLate).map { it.trip.id })
    }

    @Test
    fun fullTiesAreBrokenById() {
        val trips = listOf(trip(id = "b"), trip(id = "a"))
        assertEquals(listOf("a", "b"), useCase(trips, criteria(envigado, campus)).map { it.trip.id })
    }

    @Test
    fun driftedCampusCopyStillMatches() {
        val t = trip(destination = Place.EIA_LAS_PALMAS.copy(latitude = 6.17))
        assertEquals(0.0, useCase(listOf(t), criteria(destination = campus)).single().dropoff!!.distanceMeters)
    }

    @Test
    fun countsTripsAtOtherTimes() {
        val atOtherTime = criteria(envigado, campus).copy(departureAroundEpochMs = 99_000_000L)
        assertEquals(1, useCase.suggestRelaxation(listOf(trip()), atOtherTime).anyTimeCount)
    }

    @Test
    fun suggestsNothingWithoutATimeConstraint() {
        val veryFar = place("Rionegro", 6.15, -75.37)
        assertEquals(0, useCase.suggestRelaxation(listOf(trip()), criteria(veryFar, campus)).anyTimeCount)
    }

    @Test
    fun towardsCampusThePlaceIsWhereYouGetOn() {
        val criteria = TripSearchCriteria.forCampus(CampusDirection.ToCampus, campus, envigado)
        val match = useCase(listOf(trip()), criteria).single()
        assertEquals(envigado.name, match.pickup!!.place.name)
    }

    @Test
    fun fromCampusOnlyMatchesTripsLeavingIt() {
        val criteria = TripSearchCriteria.forCampus(CampusDirection.FromCampus, campus, envigado)
        assertTrue(useCase(listOf(trip()), criteria).isEmpty())
        val homeward = trip(origin = campus, destination = envigado, waypoints = emptyList())
        assertEquals(1, useCase(listOf(homeward), criteria).size)
    }

    @Test
    fun emptyInputsAreFine() {
        assertTrue(useCase(emptyList(), criteria(envigado, campus)).isEmpty())
        assertEquals(1, useCase(listOf(trip(waypoints = emptyList())), criteria(envigado, campus)).size)
    }
}
