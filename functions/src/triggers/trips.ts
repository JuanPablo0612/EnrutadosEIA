import {getFirestore} from "firebase-admin/firestore";
import {
  onDocumentDeleted,
  onDocumentUpdated,
} from "firebase-functions/v2/firestore";
import {Collections} from "../config";
import {
  BookingDoc,
  NotificationParams,
  NotificationType,
  SYSTEM_ACTOR,
  TripDoc,
} from "../model";
import {str, userName} from "../notifications/params";
import {notify} from "../notifications/notify";

interface AffectedBooking {
  id: string;
  passengerId: string;
}

/**
 * Cancels every open (PENDING or CONFIRMED) booking of a trip in one
 * transaction and resets its seat counter, then tells each affected
 * passenger. Safe to run more than once: a later run finds nothing open.
 * @param {string} tripId the cancelled trip
 * @param {TripDoc} trip the trip data, for notification text
 * @param {string} driverName the driver's display name
 */
export async function cancelTripCascade(
  tripId: string,
  trip: TripDoc,
  driverName: string,
): Promise<void> {
  const db = getFirestore();
  const tripRef = db.collection(Collections.trips).doc(tripId);
  const openBookings = db.collection(Collections.bookings)
    .where("tripId", "==", tripId)
    .where("status", "in", ["PENDING", "CONFIRMED"]);

  const affected = await db.runTransaction(async (tx) => {
    const snap = await tx.get(openBookings);
    const tripSnap = await tx.get(tripRef);
    snap.docs.forEach((doc) => tx.update(doc.ref, {
      status: "CANCELLED",
      rejectReason: "TRIP_CANCELLED",
      cancelledBy: SYSTEM_ACTOR,
    }));
    if (tripSnap.exists) tx.update(tripRef, {confirmedSeats: 0});
    return snap.docs.map((doc): AffectedBooking => ({
      id: doc.id,
      passengerId: str((doc.data() as BookingDoc).passengerId, 200),
    }));
  });

  await Promise.all(affected.map((booking) => notify({
    recipientId: booking.passengerId,
    type: "trip_cancelled",
    params: {...tripParams(tripId, trip), bookingId: booking.id, driverName},
    inAppId: `trip_cancelled_${tripId}`,
  })));
}

/**
 * Tells every passenger with a confirmed seat about a trip event.
 * @param {string} tripId the trip
 * @param {TripDoc} trip the trip data
 * @param {NotificationType} type trip_started or trip_completed
 */
async function notifyConfirmedPassengers(
  tripId: string,
  trip: TripDoc,
  type: NotificationType,
): Promise<void> {
  const snap = await getFirestore()
    .collection(Collections.bookings)
    .where("tripId", "==", tripId)
    .where("status", "==", "CONFIRMED")
    .get();
  if (snap.empty) return;
  const driverId = str(trip.driverId, 200);
  const driverName = await userName(driverId);
  await Promise.all(snap.docs.map((doc) => notify({
    recipientId: str((doc.data() as BookingDoc).passengerId, 200),
    type,
    params: {
      ...tripParams(tripId, trip),
      bookingId: doc.id,
      driverId,
      driverName,
    },
    inAppId: `${type}_${tripId}`,
  })));
}

/**
 * Tells every passenger with an open (pending or confirmed) booking that
 * the driver changed the trip's stops.
 * @param {string} tripId the trip
 * @param {TripDoc} trip the trip data after the change
 * @param {string} changeId identifies this change, so a retried event
 *     doesn't notify twice while a later change still does
 */
async function notifyOpenPassengersOfUpdate(
  tripId: string,
  trip: TripDoc,
  changeId: string,
): Promise<void> {
  const snap = await getFirestore()
    .collection(Collections.bookings)
    .where("tripId", "==", tripId)
    .where("status", "in", ["PENDING", "CONFIRMED"])
    .get();
  if (snap.empty) return;
  const driverId = str(trip.driverId, 200);
  const driverName = await userName(driverId);
  await Promise.all(snap.docs.map((doc) => notify({
    recipientId: str((doc.data() as BookingDoc).passengerId, 200),
    type: "trip_updated",
    params: {
      ...tripParams(tripId, trip),
      bookingId: doc.id,
      driverId,
      driverName,
    },
    inAppId: `trip_updated_${tripId}_${changeId}`,
  })));
}

/**
 * A comparable form of a trip's intermediate stops.
 * @param {TripDoc} trip the trip data
 * @return {string} the stops' names and addresses, in order
 */
function stopsKey(trip: TripDoc): string {
  return JSON.stringify(
    (trip.waypoints ?? []).map((stop) => [stop.name ?? "", stop.address ?? ""]),
  );
}

/**
 * The params every trip-related notification carries.
 * @param {string} tripId the trip id
 * @param {TripDoc} trip the trip data
 * @return {NotificationParams} trip and route params
 */
function tripParams(tripId: string, trip: TripDoc): NotificationParams {
  return {
    tripId,
    originName: str(trip.origin?.name),
    destinationName: str(trip.destination?.name),
    departureTime: str(trip.departureTime ?? ""),
  };
}

/**
 * Reacts to a driver starting, finishing or cancelling a trip, or changing
 * the stops of an active one. Most trip writes are location updates while
 * driving, which change neither and exit immediately.
 */
export const onTripUpdated = onDocumentUpdated(
  `${Collections.trips}/{tripId}`,
  async (event) => {
    const before = event.data?.before.data() as TripDoc | undefined;
    const after = event.data?.after.data() as TripDoc | undefined;
    if (!before || !after) return;
    const tripId = event.params.tripId;

    if (before.status === after.status) {
      // Passengers only need to hear about stops; a seat change affects
      // nobody already booked.
      if (after.status === "ACTIVE" && stopsKey(before) !== stopsKey(after)) {
        // The write time identifies the change and is the same on retries.
        const changedAt = event.data?.after.updateTime.toMillis() ?? 0;
        await notifyOpenPassengersOfUpdate(tripId, after, String(changedAt));
      }
      return;
    }

    switch (after.status) {
    case "CANCELLED":
      await cancelTripCascade(tripId, after, await userName(after.driverId));
      return;
    case "IN_PROGRESS":
      await notifyConfirmedPassengers(tripId, after, "trip_started");
      return;
    case "COMPLETED":
      await notifyConfirmedPassengers(tripId, after, "trip_completed");
      return;
    default:
      return;
    }
  },
);

/** A deleted trip must not leave its bookings open. */
export const onTripDeleted = onDocumentDeleted(
  `${Collections.trips}/{tripId}`,
  async (event) => {
    const trip = event.data?.data() as TripDoc | undefined;
    if (!trip || trip.status === "COMPLETED") return;
    await cancelTripCascade(
      event.params.tripId,
      trip,
      await userName(trip.driverId),
    );
  },
);
