package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.place.model.matchDistanceMeters
import com.juanpablo0612.carpool.domain.trip.model.MatchedStop
import com.juanpablo0612.carpool.domain.trip.model.SearchRelaxation
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripMatch
import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria
import kotlin.math.abs

/**
 * Matches trips to a passenger's origin/destination by geographic proximity to the trip's
 * declared stops, respecting direction: the pickup stop must come before the drop-off stop on the
 * path `[origin, waypoints…, destination]`. Also filters by time window, contribution and free
 * seats, and ranks the results.
 */
class MatchTripsUseCase {

    operator fun invoke(trips: List<Trip>, criteria: TripSearchCriteria): List<TripMatch> {
        val toleranceMs = criteria.toleranceMinutes * 60_000L
        return trips.asSequence()
            .filter { trip ->
                criteria.departureAroundEpochMs == null ||
                    abs(trip.departureTime - criteria.departureAroundEpochMs) <= toleranceMs
            }
            .filter { trip ->
                criteria.maxContribution == null ||
                    (trip.contributionPerPassenger ?: 0) <= criteria.maxContribution
            }
            .mapNotNull { trip ->
                val seats = (trip.seatCount - trip.confirmedSeats).coerceAtLeast(0)
                if (seats == 0) null else matchPath(trip, criteria)?.copy(availableSeats = seats)
            }
            .sortedWith(rankingOf(criteria))
            .toList()
    }

    /** How the search could be relaxed when [invoke] returns nothing for [criteria]. */
    fun suggestRelaxation(trips: List<Trip>, criteria: TripSearchCriteria): SearchRelaxation {
        var widerRadius: Int? = null
        var widerCount = 0
        for (radius in TripSearchCriteria.WALK_RADIUS_OPTIONS_METERS.filter { it > criteria.maxWalkMeters }) {
            val count = invoke(trips, criteria.copy(maxWalkMeters = radius)).size
            if (count > 0) {
                widerRadius = radius
                widerCount = count
                break
            }
        }
        val anyTimeCount = if (criteria.departureAroundEpochMs != null) {
            invoke(trips, criteria.copy(departureAroundEpochMs = null)).size
        } else 0
        return SearchRelaxation(widerRadius, widerCount, anyTimeCount)
    }

    private fun matchPath(trip: Trip, criteria: TripSearchCriteria): TripMatch? {
        val path = listOf(trip.origin) + trip.waypoints + trip.destination
        val radius = criteria.maxWalkMeters.toDouble()
        val origin = criteria.origin
        val destination = criteria.destination

        fun stopsNear(place: Place, indices: IntRange): List<MatchedStop> = indices.mapNotNull { i ->
            matchDistanceMeters(path[i], place)
                ?.takeIf { it <= radius }
                ?.let { MatchedStop(path[i], i, it) }
        }

        val pickupIndices = 0..path.lastIndex - 1
        val dropoffIndices = 1..path.lastIndex

        return when {
            origin != null && destination != null -> {
                val pickups = stopsNear(origin, pickupIndices)
                val dropoffs = stopsNear(destination, dropoffIndices)
                val best = pickups.flatMap { p -> dropoffs.filter { it.pathIndex > p.pathIndex }.map { p to it } }
                    .minWithOrNull(
                        compareBy<Pair<MatchedStop, MatchedStop>> { it.first.distanceMeters + it.second.distanceMeters }
                            .thenBy { it.first.distanceMeters }
                            .thenBy { it.first.pathIndex }
                    ) ?: return null
                TripMatch(trip, best.first, best.second, availableSeats = 0)
            }
            destination != null -> {
                val dropoff = stopsNear(destination, dropoffIndices)
                    .minWithOrNull(compareBy<MatchedStop> { it.distanceMeters }.thenBy { it.pathIndex })
                    ?: return null
                TripMatch(trip, pickup = null, dropoff = dropoff, availableSeats = 0)
            }
            origin != null -> {
                val pickup = stopsNear(origin, pickupIndices)
                    .minWithOrNull(compareBy<MatchedStop> { it.distanceMeters }.thenBy { it.pathIndex })
                    ?: return null
                TripMatch(trip, pickup = pickup, dropoff = null, availableSeats = 0)
            }
            else -> TripMatch(trip, pickup = null, dropoff = null, availableSeats = 0)
        }
    }

    private fun rankingOf(criteria: TripSearchCriteria): Comparator<TripMatch> {
        // 250 m buckets: a 120 m vs 180 m walk shouldn't outrank a better departure time.
        val walkBucket = compareBy<TripMatch> {
            ((it.pickup?.distanceMeters ?: 0.0) + (it.dropoff?.distanceMeters ?: 0.0)).toInt() / WALK_BUCKET_METERS
        }
        val target = criteria.departureAroundEpochMs
        val byTime = if (target != null) {
            walkBucket.thenBy { abs(it.trip.departureTime - target) }
        } else walkBucket
        return byTime.thenBy { it.trip.departureTime }.thenBy { it.trip.id }
    }

    private companion object {
        const val WALK_BUCKET_METERS = 250
    }
}
