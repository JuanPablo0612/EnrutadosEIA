import {getFirestore} from "firebase-admin/firestore";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import {Collections} from "../config";
import {RatingDoc, UserDoc} from "../model";
import {roundedAverage, validateRating} from "../ratings/validate";

/**
 * Adds a new rating to the ratee's aggregate (`ratingSum`, `ratingCount`),
 * so showing a driver's average reads one document instead of every rating.
 * Invalid ratings are marked and never counted; `aggregatedAt` on the rating
 * makes a retried event a no-op.
 */
export const onRatingCreated = onDocumentCreated(
  `${Collections.ratings}/{ratingId}`,
  async (event) => {
    const rating = event.data?.data() as RatingDoc | undefined;
    if (!rating || !event.data) return;
    const db = getFirestore();
    const ratingRef = event.data.ref;

    const valid = await validateRating(db, event.params.ratingId, rating);
    if (!valid) {
      await ratingRef.update({aggregation: "rejected"});
      return;
    }

    const userRef = db.collection(Collections.users).doc(valid.rateeId);
    await db.runTransaction(async (tx) => {
      const [current, user] = await Promise.all([
        tx.get(ratingRef),
        tx.get(userRef),
      ]);
      if ((current.data() as RatingDoc | undefined)?.aggregatedAt) return;
      if (!user.exists) {
        tx.update(ratingRef, {aggregation: "rejected"});
        return;
      }
      const data = user.data() as UserDoc;
      const sum = (data.ratingSum ?? 0) + valid.stars;
      const count = (data.ratingCount ?? 0) + 1;
      tx.update(userRef, {
        ratingSum: sum,
        ratingCount: count,
        ratingAverage: roundedAverage(sum, count),
      });
      tx.update(ratingRef, {aggregatedAt: Date.now()});
    });
  },
);
