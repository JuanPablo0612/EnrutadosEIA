import {Firestore} from "firebase-admin/firestore";
import {Collections} from "../config";
import {BookingDoc, RatingDoc} from "../model";

/** A rating whose fields have all been checked. */
export interface ValidRating {
  bookingId: string;
  raterId: string;
  rateeId: string;
  stars: number;
}

/**
 * Whether a rating may count towards the ratee's average: 1-5 whole stars,
 * id `{bookingId}_{raterId}` (one rating per person per booking), and rater
 * and ratee are the two parties of that booking.
 * @param {Firestore} db the Firestore instance
 * @param {string} ratingId the rating document id
 * @param {RatingDoc} rating the rating data
 * @return {Promise<ValidRating | null>} the rating, or null if invalid
 */
export async function validateRating(
  db: Firestore,
  ratingId: string,
  rating: RatingDoc,
): Promise<ValidRating | null> {
  const {stars, raterId, rateeId, bookingId} = rating;
  if (stars === undefined || !Number.isInteger(stars)) return null;
  if (stars < 1 || stars > 5) return null;
  if (!raterId || !rateeId || !bookingId || raterId === rateeId) return null;
  if (ratingId !== `${bookingId}_${raterId}`) return null;

  const bookingSnap = await db
    .collection(Collections.bookings)
    .doc(bookingId)
    .get();
  const booking = bookingSnap.data() as BookingDoc | undefined;
  if (!booking) return null;
  const parties = new Set([booking.passengerId, booking.driverId]);
  if (!parties.has(raterId) || !parties.has(rateeId)) return null;
  return {bookingId, raterId, rateeId, stars};
}

/**
 * Average rounded to two decimals, for server-side sorting.
 * @param {number} sum total stars
 * @param {number} count number of ratings
 * @return {number} the rounded average
 */
export function roundedAverage(sum: number, count: number): number {
  return Math.round((sum / count) * 100) / 100;
}
