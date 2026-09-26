import {getFirestore} from "firebase-admin/firestore";
import {Collections} from "../config";
import {BookingDoc, NotificationParams, UserDoc} from "../model";

const MAX_NAME_LENGTH = 60;

/**
 * Coerces a value to a trimmed string, capped at [maxLength].
 * @param {unknown} value any value, possibly missing
 * @param {number} maxLength maximum length kept
 * @return {string} the string form, or "" when missing
 */
export function str(value: unknown, maxLength = MAX_NAME_LENGTH): string {
  if (value === undefined || value === null) return "";
  return String(value).trim().slice(0, maxLength);
}

/**
 * The params every booking-related notification carries.
 * @param {string} bookingId the booking document id
 * @param {BookingDoc} booking the booking data
 * @return {NotificationParams} trip and route params
 */
export function bookingParams(
  bookingId: string,
  booking: BookingDoc,
): NotificationParams {
  return {
    bookingId,
    tripId: str(booking.tripId),
    originName: str(booking.originName),
    destinationName: str(booking.destinationName),
    departureTime: str(booking.departureTime ?? ""),
  };
}

/**
 * Reads a user's display name, or "" if the profile is missing.
 * @param {string | undefined} uid the user id
 * @return {Promise<string>} the name, capped for notification text
 */
export async function userName(uid: string | undefined): Promise<string> {
  if (!uid) return "";
  const snap = await getFirestore()
    .collection(Collections.users)
    .doc(uid)
    .get();
  return str((snap.data() as UserDoc | undefined)?.name);
}
