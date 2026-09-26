import {getFirestore} from "firebase-admin/firestore";
import {
  onDocumentCreated,
  onDocumentUpdated,
} from "firebase-functions/v2/firestore";
import {Collections} from "../config";
import {BookingDoc, SYSTEM_ACTOR, TripDoc} from "../model";
import {bookingParams, str, userName} from "../notifications/params";
import {notify} from "../notifications/notify";

/**
 * A new seat request: tells the driver, unless the trip can no longer be
 * booked, in which case the request is cancelled straight away.
 */
export const onBookingCreated = onDocumentCreated(
  `${Collections.bookings}/{bookingId}`,
  async (event) => {
    const booking = event.data?.data() as BookingDoc | undefined;
    if (!booking || booking.status !== "PENDING") return;
    const bookingId = event.params.bookingId;

    const tripSnap = await getFirestore()
      .collection(Collections.trips)
      .doc(str(booking.tripId, 200))
      .get();
    const trip = tripSnap.data() as TripDoc | undefined;
    if (!trip || trip.status !== "ACTIVE") {
      await event.data?.ref.update({
        status: "CANCELLED",
        rejectReason: "TRIP_CANCELLED",
        cancelledBy: SYSTEM_ACTOR,
      });
      return;
    }

    await notify({
      recipientId: str(booking.driverId, 200),
      type: "new_booking_request",
      params: {
        ...bookingParams(bookingId, booking),
        passengerName: str(booking.passengerName),
      },
      inAppId: `new_booking_request_${bookingId}`,
    });
  },
);

/**
 * A user changed a booking's status: tells the other party. Changes made by
 * the backend ("system") send their own notifications where they happen.
 */
export const onBookingUpdated = onDocumentUpdated(
  `${Collections.bookings}/{bookingId}`,
  async (event) => {
    const before = event.data?.before.data() as BookingDoc | undefined;
    const after = event.data?.after.data() as BookingDoc | undefined;
    if (!before || !after || before.status === after.status) return;
    if (after.cancelledBy === SYSTEM_ACTOR) return;

    const bookingId = event.params.bookingId;
    const params = bookingParams(bookingId, after);
    const passengerId = str(after.passengerId, 200);
    const driverId = str(after.driverId, 200);

    switch (after.status) {
    case "CONFIRMED":
      await notify({
        recipientId: passengerId,
        type: "booking_accepted",
        params: {...params, driverName: await userName(driverId)},
        inAppId: `booking_accepted_${bookingId}`,
      });
      return;
    case "REJECTED":
      await notify({
        recipientId: passengerId,
        type: "booking_rejected",
        params: {
          ...params,
          driverName: await userName(driverId),
          rejectReason: str(after.rejectReason),
        },
        inAppId: `booking_rejected_${bookingId}`,
      });
      return;
    case "CANCELLED": {
      const wasActive =
        before.status === "PENDING" || before.status === "CONFIRMED";
      if (!wasActive) return;
      // Builds without cancelledBy only let passengers cancel from the
      // client, so a missing actor is treated as the passenger.
      const byDriver = after.cancelledBy === driverId;
      if (byDriver) {
        await notify({
          recipientId: passengerId,
          type: "booking_cancelled_by_driver",
          params: {...params, driverName: await userName(driverId)},
          inAppId: `booking_cancelled_by_driver_${bookingId}`,
        });
      } else {
        await notify({
          recipientId: driverId,
          type: "booking_cancelled_by_passenger",
          params: {...params, passengerName: str(after.passengerName)},
          inAppId: `booking_cancelled_by_passenger_${bookingId}`,
        });
      }
      return;
    }
    default:
      return;
    }
  },
);
