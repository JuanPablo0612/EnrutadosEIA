This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Deploying Firestore rules and indexes

`firestore.rules` and `firestore.indexes.json` at the repo root define the security rules and
composite indexes every repository's `where` query relies on. They are **not** applied
automatically — you deploy them with the [Firebase CLI](https://firebase.google.com/docs/cli):

```shell
npm install -g firebase-tools   # once
firebase login
firebase deploy --only firestore:rules,firestore:indexes
```

The CLI needs a `firebase.json` associating your project with these two files. If one doesn't
exist yet, generate it once with `firebase init firestore` (accept the default file names,
`firestore.rules` and `firestore.indexes.json`, so it points at the files already in this repo),
or create it by hand:

```json
{
  "firestore": {
    "rules": "firestore.rules",
    "indexes": "firestore.indexes.json"
  }
}
```

Composite indexes can take several minutes to build after deploying; queries that need one will
fail with `FAILED_PRECONDITION` until the build finishes (the error includes a direct link to
create the missing index from the Firebase console, if you'd rather do it that way).

**Existing `places` documents predate the `ownerId` field added in Phase 1.** `ownerId` defaults
to `""` on any document written before that change, which does not match any signed-in user's
UID — those places become permanently invisible (not just to other users, to their original owner
too) once `firestore.rules` is deployed. Backfill `ownerId` on existing documents before deploying
to a database with real data, or accept the loss on a dev/test project.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…