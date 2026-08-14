# Enrutados EIA

Enrutados EIA is a carpooling app for Universidad EIA students and staff. Drivers publish
recurring routes and one-off trips; passengers search those trips and reserve seats. It's built
with Kotlin Multiplatform and Compose Multiplatform, backed by Firebase (Auth, Firestore, and
Storage), and follows Clean Architecture with MVVM on the presentation layer.

The project is two modules:

* [/androidApp](./androidApp/src) — the Android application shell. This is what builds into the
  installable APK.
* [/composeApp](./composeApp/src) — the shared Kotlin Multiplatform library holding all app code
  (UI, domain logic, data layer). [commonMain](./composeApp/src/commonMain/kotlin) is where nearly
  everything lives today.

An [/iosApp](./iosApp/iosApp) scaffold and an `iosMain` source set exist but are not currently
active — iOS is not a supported target yet.

### First-time setup

Before building, add two gitignored files that a fresh clone doesn't have:

1. **`androidApp/google-services.json`** — download it from the Firebase console (Project settings
   → your Android app, package `com.juanpablo0612.carpool`) and place it at that exact path. The
   `googleServices` Gradle plugin fails the build without it.
2. **`secrets.properties`** at the repo root — copy `secrets.properties.example` to
   `secrets.properties` and fill in a real Google Maps API key as `MAPS_API_KEY`. It's read by
   `androidApp/build.gradle.kts` and injected into the manifest as a placeholder; without it (or
   with a blank key) the build still succeeds but the key resolves to an empty string and every
   map screen renders blank.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal. Note `composeApp` is a shared **library**
module, not an application — the installable APK is built from `androidApp`:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

There is currently no test command — Android host tests are not enabled and the project has no
tests. `./gradlew :composeApp:compileCommonMainKotlinMetadata` compiles the shared `commonMain`
source set only (useful without an Android SDK installed) and is the closest thing to a CI check
today.

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Deploying Firestore and Storage rules, and Firestore indexes

`firestore.rules` / `firestore.indexes.json` (Firestore security rules and the composite indexes
every repository's `where` query relies on) and `storage.rules` (Storage security rules for the
`users/{uid}/profile.jpg` and `vehicles/{driverId}/{vehicleId}.jpg` paths written by
`FirebaseAuthRemoteDataSource` and `VehicleRepositoryImpl`) live at the repo root. They are **not**
applied automatically — deploy them with the [Firebase CLI](https://firebase.google.com/docs/cli):

```shell
npm install -g firebase-tools   # once
firebase login
firebase use enrutados-eia
firebase deploy --only firestore:rules,firestore:indexes,storage
```

(Note `storage` has no `:rules` suffix — unlike Firestore, Storage doesn't split into separate
rules/indexes deploy targets, so `--only storage:rules` fails with a "could not find rules for
storage target: rules" error.)

`.firebaserc` (project alias → `enrutados-eia`) and `firebase.json` (associating the project with
these three rules files, plus emulator ports for local dev) are already checked into the repo, so
no `firebase init` is needed. To run the emulator suite locally instead of hitting production:

```shell
firebase emulators:start
```

Composite indexes can take several minutes to build after deploying; queries that need one will
fail with `FAILED_PRECONDITION` until the build finishes (the error includes a direct link to
create the missing index from the Firebase console, if you'd rather do it that way).

**Two Firestore fields predate the rules and silently misbehave on existing documents — backfill
both before deploying to a database with real data, or accept the loss on a dev/test project:**

- **`places.ownerId`** defaults to `""` on any document written before that field existed, which
  does not match any signed-in user's UID — those places become permanently invisible (not just to
  other users, to their original owner too) once `firestore.rules` is deployed.
- **`trips.confirmedSeats`** defaults to `0` on any trip that already had confirmed passengers
  before the seat counter existed, which over-reports availability and allows overbooking until a
  booking status change next touches the counter. Backfill it as
  `count(bookings where tripId == trip.id and status == 'CONFIRMED')`.