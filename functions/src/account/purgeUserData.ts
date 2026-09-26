import {
  FieldValue,
  Firestore,
  getFirestore,
  Query,
} from "firebase-admin/firestore";
import {getStorage} from "firebase-admin/storage";
import * as logger from "firebase-functions/logger";
import {Collections} from "../config";
import {BookingDoc, SYSTEM_ACTOR, TripDoc} from "../model";
import {bookingParams, str, userName} from "../notifications/params";
import {notify} from "../notifications/notify";
import {cancelTripCascade} from "../triggers/trips";

/**
 * Removes or anonymizes everything a user left behind. Every step is
 * idempotent, so a partial failure can simply be retried.
 *
 * Other people's history stays meaningful: bookings are kept (with the
 * user's personal data blanked), past trips are kept, and ratings the user
 * gave keep counting towards others' averages.
 * @param {string} uid the user being deleted
 */
export async function purgeUserData(uid: string): Promise<void> {
  const db = getFirestore();
  const name = await userName(uid);

  await cancelOpenTrips(db, uid, name);
  await cancelOpenBookings(db, uid);
  await anonymizeBookingsAndDeleteChats(db, uid);
  await scrubPastTrips(db, uid);

  const writer = db.bulkWriter();
  for (const query of [
    db.collection(Collections.routes).where("driverId", "==", uid),
    db.collection(Collections.vehicles).where("driverId", "==", uid),
    db.collection(Collections.places).where("ownerId", "==", uid),
    db.collection(Collections.ratings).where("rateeId", "==", uid),
  ]) {
    (await query.get()).docs.forEach((doc) => writer.delete(doc.ref));
  }
  const given = await db.collection(Collections.ratings)
    .where("raterId", "==", uid).get();
  given.docs.forEach((doc) =>
    writer.update(doc.ref, {comment: null, raterDeleted: true}));
  await writer.close();

  await db.recursiveDelete(db.collection(Collections.notifications).doc(uid));
  await db.recursiveDelete(db.collection(Collections.users).doc(uid));

  const bucket = getStorage().bucket();
  await Promise.all([
    bucket.deleteFiles({prefix: `users/${uid}/`}),
    bucket.deleteFiles({prefix: `vehicles/${uid}/`}),
  ]);
  logger.info("purged user data", {uid});
}

/**
 * Cancels the trips the user still had to drive, which also cancels and
 * notifies their passengers.
 * @param {Firestore} db the Firestore instance
 * @param {string} uid the user
 * @param {string} name the user's display name
 */
async function cancelOpenTrips(
  db: Firestore,
  uid: string,
  name: string,
): Promise<void> {
  const trips = await db.collection(Collections.trips)
    .where("driverId", "==", uid)
    .where("status", "in", ["ACTIVE", "IN_PROGRESS"])
    .get();
  for (const doc of trips.docs) {
    await doc.ref.update({status: "CANCELLED"});
    await cancelTripCascade(doc.id, doc.data() as TripDoc, name);
  }
}

/**
 * Withdraws the user's open seat requests, freeing confirmed seats, and
 * tells each driver.
 * @param {Firestore} db the Firestore instance
 * @param {string} uid the user
 */
async function cancelOpenBookings(db: Firestore, uid: string): Promise<void> {
  const bookings = await db.collection(Collections.bookings)
    .where("passengerId", "==", uid)
    .where("status", "in", ["PENDING", "CONFIRMED"])
    .get();
  for (const doc of bookings.docs) {
    const cancelled = await db.runTransaction(async (tx) => {
      const current = (await tx.get(doc.ref)).data() as BookingDoc | undefined;
      const open = current?.status === "PENDING" ||
        current?.status === "CONFIRMED";
      if (!current || !open) return null;
      const tripRef = db.collection(Collections.trips)
        .doc(str(current.tripId, 200));
      const trip = (await tx.get(tripRef)).data() as TripDoc | undefined;
      tx.update(doc.ref, {status: "CANCELLED", cancelledBy: SYSTEM_ACTOR});
      const tripOpen = trip?.status === "ACTIVE" ||
        trip?.status === "IN_PROGRESS";
      if (current.status === "CONFIRMED" && tripOpen) {
        tx.update(tripRef, {confirmedSeats: FieldValue.increment(-1)});
      }
      return current;
    });
    if (!cancelled) continue;
    await notify({
      recipientId: str(cancelled.driverId, 200),
      type: "booking_cancelled_by_passenger",
      params: {
        ...bookingParams(doc.id, cancelled),
        passengerName: str(cancelled.passengerName),
      },
      inAppId: `booking_cancelled_by_passenger_${doc.id}`,
    });
  }
}

/**
 * Blanks the user's personal data on every booking they were part of and
 * deletes those bookings' chats.
 * @param {Firestore} db the Firestore instance
 * @param {string} uid the user
 */
async function anonymizeBookingsAndDeleteChats(
  db: Firestore,
  uid: string,
): Promise<void> {
  const asPassenger = await db.collection(Collections.bookings)
    .where("passengerId", "==", uid).get();
  const asDriver = await db.collection(Collections.bookings)
    .where("driverId", "==", uid).get();

  const writer = db.bulkWriter();
  asPassenger.docs.forEach((doc) => writer.update(doc.ref, {
    passengerName: "",
    passengerEmail: "",
    passengerMessage: null,
    passengerDeleted: true,
  }));
  asDriver.docs.forEach((doc) =>
    writer.update(doc.ref, {driverDeleted: true}));
  await writer.close();

  const bookingIds = [...asPassenger.docs, ...asDriver.docs].map((d) => d.id);
  for (const bookingId of new Set(bookingIds)) {
    await db.recursiveDelete(db.collection(Collections.chats).doc(bookingId));
  }
}

/**
 * Keeps finished trips for passengers' history but drops the driver's last
 * location and message.
 * @param {Firestore} db the Firestore instance
 * @param {string} uid the user
 */
async function scrubPastTrips(db: Firestore, uid: string): Promise<void> {
  const trips: Query = db.collection(Collections.trips)
    .where("driverId", "==", uid);
  const writer = db.bulkWriter();
  (await trips.get()).docs.forEach((doc) => writer.update(doc.ref, {
    driverLatitude: null,
    driverLongitude: null,
    messageToPassengers: "",
  }));
  await writer.close();
}
