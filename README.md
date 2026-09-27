# Enrutados EIA

Enrutados EIA is a carpooling app for Universidad EIA students and staff. Anyone can search for
trips that pass near them and reserve seats; anyone with a registered vehicle can publish trips —
one at a time or a whole week of a recurring route. There are no user roles. It's built with
Kotlin Multiplatform and Compose Multiplatform, backed by Firebase (Auth, Firestore, Storage,
Cloud Messaging and Cloud Functions — see [Cloud Functions](#cloud-functions)), and follows Clean Architecture with MVVM on the presentation layer: ten singular
feature packages (`auth, booking, chat, notification, place, preferences, rating, route, trip,
vehicle`) live in parallel under `data/`, `domain/`, and `presentation/`, each with a
`datasource/` that owns every Firebase call, a `repository/` that maps DTOs to domain models and
translates failures to `AppException`, and a `domain/{feature}/usecase/` that holds only the use
cases with real orchestration or derivation logic — plain single-call reads and writes go straight
from ViewModel to repository instead.

The project is two modules:

* [/androidApp](./androidApp/src) — the Android application shell. This is what builds into the
  installable APK.
* [/composeApp](./composeApp/src) — the shared Kotlin Multiplatform library holding all app code
  (UI, domain logic, data layer). [commonMain](./composeApp/src/commonMain/kotlin) is where nearly
  everything lives today.

Unit tests for the domain logic live in `composeApp/src/commonTest` and run on the JVM with
`./gradlew :composeApp:testAndroidHostTest`. The backend lives in [/functions](./functions/src).

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
`FirebaseAuthRemoteDataSource` and `FirebaseVehicleStorageDataSource`) live at the repo root. They
are **not** applied automatically — deploy them with the
[Firebase CLI](https://firebase.google.com/docs/cli):

```shell
npm install -g firebase-tools   # once
firebase login
firebase use enrutados-eia
firebase deploy --only firestore:rules,firestore:indexes,storage
```

To check the rules compile without deploying anything: `firebase deploy --only firestore:rules --dry-run`.

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

The app is not in production, so the code carries no compatibility layer for older document
shapes: when the schema changes, the code moves to the new shape without migrations.

### Cloud Functions

`functions/` (TypeScript, Node 22, 2nd-gen Firebase Functions) is the app's backend: it writes
in-app notifications and sends push notifications, cascades trip cancellations to their bookings,
keeps each user's rating aggregate, and purges a user's data when their account is deleted.
Deploying functions requires the project to be on the **Blaze** (pay-as-you-go) plan — set a
budget alert in the Google Cloud console when upgrading.

```shell
npm --prefix functions ci
npm --prefix functions run lint
npm --prefix functions run build
firebase deploy --only functions
```

`firebase.json` runs lint and build as predeploy steps. Functions deploy to `us-central1`, which
must match the Firestore location (`nam5`) and `FUNCTIONS_REGION` in the app's `BackendConfig.kt`.
`firebase emulators:start` also starts the functions emulator; there, push notifications are
logged instead of sent.

**First deployment, in this order:**

1. Upgrade the `enrutados-eia` project to Blaze and make sure the *Firebase Cloud Messaging API
   (V1)* is enabled in the Google Cloud console.
2. `npm --prefix functions ci`
3. `firebase deploy --only firestore:indexes` and wait until the indexes finish building.
4. `firebase deploy --only functions` (accept the Artifact Registry cleanup-policy prompt; if the
   first 2nd-gen deploy fails with an Eventarc service-agent permission error, retry after a few
   minutes).
5. `firebase deploy --only firestore:rules,storage`.
6. Install the new app build.
