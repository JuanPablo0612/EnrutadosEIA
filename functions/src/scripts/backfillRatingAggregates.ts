/**
 * One-off backfill: recomputes every user's rating aggregate from scratch
 * and marks each counted rating, so onRatingCreated never double-counts.
 * Safe to re-run.
 *
 *   gcloud auth application-default login
 *   npm --prefix functions run backfill:ratings -- --dry-run
 *   npm --prefix functions run backfill:ratings
 *
 * Honors FIRESTORE_EMULATOR_HOST to run against the emulator.
 */
import {initializeApp} from "firebase-admin/app";
import {getFirestore} from "firebase-admin/firestore";
import {Collections} from "../config";
import {RatingDoc} from "../model";
import {roundedAverage, validateRating} from "../ratings/validate";

/** Runs the backfill. */
async function main(): Promise<void> {
  const dryRun = process.argv.includes("--dry-run");
  initializeApp({projectId: "enrutados-eia"});
  const db = getFirestore();

  const totals = new Map<string, {sum: number; count: number}>();
  const counted: string[] = [];
  const ratings = await db.collection(Collections.ratings).get();
  for (const doc of ratings.docs) {
    const valid = await validateRating(db, doc.id, doc.data() as RatingDoc);
    if (!valid) continue;
    const total = totals.get(valid.rateeId) ?? {sum: 0, count: 0};
    total.sum += valid.stars;
    total.count += 1;
    totals.set(valid.rateeId, total);
    counted.push(doc.id);
  }

  console.log(`${counted.length}/${ratings.size} valid ratings, ` +
    `${totals.size} users`);
  if (dryRun) return;

  const writer = db.bulkWriter();
  for (const [uid, {sum, count}] of totals) {
    const userRef = db.collection(Collections.users).doc(uid);
    if (!(await userRef.get()).exists) continue;
    writer.update(userRef, {
      ratingSum: sum,
      ratingCount: count,
      ratingAverage: roundedAverage(sum, count),
    });
  }
  const now = Date.now();
  for (const id of counted) {
    writer.update(db.collection(Collections.ratings).doc(id), {
      aggregatedAt: now,
    });
  }
  await writer.close();
  console.log("Done.");
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
