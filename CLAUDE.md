# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. `AGENTS.md` holds the same rules for other agents; keep the two in sync.

## Project

EnrutadosEIA — a Kotlin Multiplatform / Compose Multiplatform carpooling app for university students (EIA, Envigado). Anyone can search and book seats; anyone with a registered vehicle can publish trips. There are **no user roles** — one account, one navigation. Targets Android and iOS (16+).

## Build, Test & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Clean build
./gradlew clean :androidApp:assembleDebug

# Unit tests (commonTest, run on the JVM via Android host tests — no device needed)
./gradlew :composeApp:testAndroidHostTest

# Compile shared code only (no Android SDK required)
./gradlew :composeApp:compileCommonMainKotlinMetadata

# Type-check the iOS Kotlin code (works on any host; linking and running need a Mac)
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Cloud Functions
npm --prefix functions run lint
npm --prefix functions run build
```

On Windows use `gradlew.bat`. Two-module project: `androidApp` (Android application shell) + `composeApp` (shared KMP **library** — `:composeApp:assembleDebug` is not a valid task; the APK comes from `:androidApp:assembleDebug`). The iOS app is the Xcode project `iosApp/iosApp.xcodeproj` (scheme `iosApp`, Xcode 26.4.1+, on a Mac); its "Compile Kotlin Framework" phase runs `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`. Gradle configuration cache is enabled. JVM target is 17. There is no CI workflow and no lint/detekt/ktlint config for Kotlin.

**Tests** live in `composeApp/src/commonTest` (kotlin-test + kotlinx-coroutines-test) and cover pure domain logic: trip matching (`MatchTripsUseCase`, `GeoDistance`), recurring slots, trip validation, the publish and update-trip use cases (with in-memory fakes in `PublishFakes.kt`), notification deep links, push payload parsing (`PushPayload`) and push token registration (`pushTokenActions`). Add tests for any new domain logic; there are no UI/instrumented tests.

### First-time setup

A fresh clone is missing two gitignored files the Android build needs:

1. **`androidApp/google-services.json`** — Firebase config for the `com.juanpablo0612.carpool` app. Download it from the Firebase console (Project settings → your Android app) and place it at that exact path. Without it, the `googleServices` Gradle plugin fails the build.
2. **`secrets.properties`** at the repo root — holds `MAPS_API_KEY`, consumed by `androidApp/build.gradle.kts` (manifest placeholder) and by `composeApp/build.gradle.kts` via BuildKonfig as `com.juanpablo0612.carpool.core.config.BuildKonfig.MAPS_API_KEY`, used for Places API (New) autocomplete/details and Geocoding (`data/place/datasource/GooglePlacesSearchService.kt`). Copy `secrets.properties.example` and fill in a key with **Maps SDK for Android**, **Places API (New)** and **Geocoding API** enabled. Without it the build succeeds but maps render blank and address search returns nothing. iOS uses the same key for the Maps SDK, so also enable **Maps SDK for iOS** on it.

The iOS build additionally needs (all gitignored or outside the repo):

1. **`iosApp/iosApp/GoogleService-Info.plist`** — from the Firebase iOS app with bundle ID `com.juanpablo0612.carpool`. The "Check Firebase config" build phase fails the build without it.
2. **`iosApp/Configuration/Local.xcconfig`** — copy `Local.xcconfig.example` and set `TEAM_ID` (Apple Developer team) for automatic signing of the app and its `.NotificationService` extension, with the Push Notifications and App Groups capabilities. Push needs the paid Apple Developer Program; a free Personal Team can use the overrides commented in the example to build without push and the app group.
3. An **APNs authentication key** uploaded in the Firebase console (Project settings → Cloud Messaging → Apple app configuration); without it FCM cannot reach iOS devices.

Xcode resolves the Swift packages on first open; commit the generated `iosApp/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved`.

### Backend: Cloud Functions, rules, indexes

`functions/` (TypeScript, Node 22, firebase-functions 7 / firebase-admin 13, eslint-config-google — JSDoc on every function, max 80 columns) is the backend. It **owns** these writes; the app must never do them itself:

- **Notifications** — in-app documents in `notifications/{uid}/items` (type + string params, deterministic ids for idempotency) and FCM pushes (data-only on Android; on iOS an `apns` alert with a generic loc-key text and `mutable-content`, rewritten on the device by the NotificationService extension), sent from Firestore triggers on bookings, trips and chat messages (`functions/src/triggers`, `functions/src/notifications`). Clients may only read, mark read and delete their own notifications.
- **Trip cancellation cascade** — cancelling or deleting a trip cancels its open bookings (`cancelledBy: "system"`) and resets `confirmedSeats`.
- **Rating aggregate** — `users/{uid}.ratingSum` / `ratingCount` (integers; the app computes the average).
- **Account deletion** — the `deleteAccount` callable purges the user's data and then the auth user (plus a 1st-gen `auth.user().onDelete` safety net).

Functions deploy to `us-central1`, which must match the Firestore location (`nam5`) and `BackendConfig.FUNCTIONS_REGION` in the app. Deploying needs the Blaze plan.

`firestore.rules`, `firestore.indexes.json`, `storage.rules` and `firebase.json` are at the repo root; `.firebaserc` points at `enrutados-eia` (no `firebase init` needed). Deploy with `firebase deploy --only functions,firestore:rules,firestore:indexes,storage` (Storage has no `:rules` sub-target). `firebase deploy --only firestore:rules --dry-run` validates the rules without deploying. See `README.md` for the first-deployment order.

## Architecture

**Clean Architecture + MVVM**, organized as hybrid layers + features. Feature packages are singular and identical across `data/`, `domain/`, and `presentation/`: `auth, booking, chat, notification, place, preferences, rating, route, trip, vehicle` (`route` is a driver's saved route template; `trip` is a bookable trip with date, vehicle and seats — separate features on purpose; a one-off trip has `routeId == ""`).

```
com/juanpablo0612/carpool/
├── core/config/             # FeatureFlags, BackendConfig, generated BuildKonfig
├── core/exception/          # AppException sealed class (domain-safe errors)
├── data/{feature}/
│   ├── model/               # DTOs (absent for `preferences`, which has no Firestore DTO)
│   ├── datasource/          # owns every Firebase/DataStore call; throws
│   └── repository/          # DTO→domain mapping, catches and returns Result<T>
├── domain/{feature}/
│   ├── model/               # domain models
│   ├── repository/          # repository interfaces
│   ├── usecase/             # only where there is real logic (see below)
│   └── validation/          # pure validators (auth/Validator, trip/TripDraftValidator)
├── presentation/{feature}/  # Screens, ViewModels, UiState, Actions, screen-local errors
│   └── .../components/      # leaf composables for screens over ~250 lines
├── presentation/navigation/ # Route.kt + graph/ (see Navigation below)
├── presentation/ui/         # components/ used by 2+ features, theme, util/
└── di/                      # one Koin module file per feature (see DI below)
```

Non-feature presentation packages: `home` (Inicio), `mytrips` (Mis viajes tab host), `onboarding`, `profile`, `session`, `splash`. Shared presentation pieces worth knowing: `presentation/place/stops/` (StopsDraft, SelectionTarget, `stopsEditorItems`/`stopsReadOnlyItems`, `StopSelectionHost` — the stop editor used by route create/edit and trip publishing), `presentation/notification/NotificationText.kt` (`resolveNotificationText`, shared by the in-app list and the push renderer). Android-only push code lives in `composeApp/src/androidMain/.../push/`, iOS push glue in `composeApp/src/iosMain/.../push/`; both parse the backend's envelope with the shared `data/notification/model/PushPayload`.

**Dependency rule:** presentation → domain ← data. Domain is pure Kotlin with no framework imports.

## Key Patterns

**Screen structure** — every screen has two composables:
1. `XxxScreen` — injects ViewModel, collects state, handles navigation side-effects
2. `XxxContent` — stateless, receives state + callbacks, supports `@Preview`

**ViewModel** — one per screen. Exposes `StateFlow<UiState>` + `SharedFlow<Event>`. UI calls `onAction(Action)`. No business logic in ViewModels. `BookingWithPassenger`, `PassengerSummary`, and `TripSummary` live in `presentation/booking/model/` since a ViewModel builds them and only Compose consumes them. `UserSession` (in-memory signed-in user) is reloaded in `Navigation.kt` after process death.

**Use cases** — 19 total, kept only where there is real logic: orchestration across repositories, derivation, ownership checks, validation, or entity construction (`booking`: CreateBooking, CheckExistingBooking, GetTripAvailableSeats, GetBookingsForTrip, RejectBooking, ConfirmBooking, CancelBooking · `place`: CreatePlace, DeletePlace, GetSavedPlaces · `route`: DuplicateRoute · `trip`: GetAvailableTrips, MatchTrips, GenerateRecurringTripSlots, PublishTrip, PublishRecurringTrips, UpdateTrip · `chat`: SendMessage · `rating`: CreateRating). For a plain single-call read or write, the ViewModel injects the repository directly. Pure validators (`domain/auth/validation/Validator.kt`, `domain/trip/validation/TripDraftValidator.kt`) are called directly; time-dependent domain logic takes `now` and the time zone as parameters so it stays testable.

**Error handling** — one rule: **repositories fail with `AppException`; presentation owns every error type the UI renders.** Every repository maps raw SDK/Firebase exceptions to a domain-safe subclass of `core/exception/AppException.kt` (one nested sealed class per feature) — `Result.failure(e)` with a raw exception is never returned. Firestore-backed features mostly produce `.Unknown`, because the `dev.gitlive` Firestore SDK exposes no exception subtypes; `TripException` also carries use-case outcomes (`Invalid(errors)`, `NotAuthenticated`, `VehicleNotFound`, `RouteSavedTripFailed`). Two error-typing styles coexist in `presentation/{feature}/`:
  - **Sealed class + mapper** (`AuthError`, `BookingError`, `TripError`, `RatingError`, `NotificationError`, each with an `XxxErrorMapper.kt` holding `Throwable.toXxxError()` / `XxxError.asStringResource()`; `TripErrorMapper` also maps `TripValidationError`).
  - **Self-contained presentation error class** (`AddPlaceError`, `CreateRouteError`, `RegisterVehicleError`, `EditProfileFieldError`, `HomeError`, `RouteDetailError`, `TripTrackingError`) for screen-local errors, with a member `asStringResource()`.

  Errors reach the UI as `ErrorMessage`/`ErrorState` components or inline field errors — **never SnackBars, never a raw `throwable.message`.** Forms show validation problems next to each field when the primary button is tapped rather than silently disabling it.

**Navigation** — type-safe routes via one flat `@Serializable sealed interface Route` (31 routes) in `presentation/navigation/Route.kt`. One set of tabs for everyone: **Inicio · Buscar · Mis viajes · Perfil** (`BottomNavItem`), shown by `NavigationSuiteScaffoldLayout` as the bottom bar on phones and `NavigationRailBar` on medium/expanded windows. Always switch tabs with `NavHostController.navigateToTopLevel` (saves/restores each tab's stack, Home is the root); never push a tab destination. `presentation/navigation/graph/`: `RootNavGraph` (Splash, Onboarding), `AuthNavGraph` (Entry — where signed-out users start, asking whether they have an account — then Login, Register, ForgotPassword, EmailVerification), `MainNavGraph` (Home, SearchTrips, MyTrips, TripDetailPassenger), `DriverNavGraph` (screens for trips you drive — routes, publishing and editing trips, vehicles, booking requests — reachable by everyone) and `SharedNavGraph` (profile, places, notifications, chat, tracking, rating). `Navigation.kt` assembles the `NavHost`, bottom bar and badges, logout, and push-tap handling (`PendingDeepLinks`). Notification deep links are plain strings built and parsed in `NotificationDeepLink.kt` (`forNotification(type, params)` / `toRouteOrNull()`), opened with `navigateToNotificationDeepLink`. One-time events use `ObserveAsEvents` (in `presentation/ui/util/`) with `SharedFlow`.

**Notifications (client side)** — the app never creates notifications. It renders them from `type` + `params` with `resolveNotificationText`. `PushTokenSync` registers the device's FCM token under `users/{uid}/fcmTokens` on sign-in; sign-out removes it. `CarpoolMessagingService` shows data-only pushes in the device language; on iOS the `NotificationService` app extension rewrites the backend's alert before display (`iosApp/NotificationService/NotificationText.swift` mirrors `resolveNotificationText`, with its strings in `iosApp/Shared/Localizable.xcstrings`, same keys and texts as composeResources) and `IosPushNotifications` decides foreground banners and handles taps. A new or changed notification kind therefore touches `NotificationType.kt`/`NotificationText.kt`/`strings.xml`, `functions/src/model.ts`, `NotificationText.swift` and `Localizable.xcstrings`. The notification permission (POST_NOTIFICATIONS on Android, `UNUserNotificationCenter` authorization on iOS) is requested via `rememberNotificationPermissionState()` right after a meaningful action (requesting a seat, publishing a trip), never at launch.

**iOS** — `iosApp/` is a SwiftUI shell around `MainViewController()`. `AppDelegate.swift` configures Firebase (`GoogleService-Info.plist`) and the Maps SDK, then calls `IosApp.start` (iosMain; Koin + `PushTokenSync`, the counterpart of `CarpoolApplication`); `AppDelegate+Notifications.swift` hands the APNs token to FCM (swizzling is off) and forwards foreground pushes and taps to `IosPushNotifications`. Native SDKs are Swift packages in the Xcode project, not CocoaPods: `firebase-ios-sdk` 11.x (the version the gitlive 2.x cinterop is built against — don't jump to 12.x without upgrading gitlive) and `ios-maps-sdk`. Kotlin can't import them, so Google Maps views are written in Swift (`iosApp/iosApp/Maps/`), implement the Kotlin interfaces in `NativeMapViews.kt`, and are embedded by the iOS `MapPreview`/`MapRoutePreview` through `UIKitView`. Without a Maps key (`BuildKonfig.MAPS_API_KEY` empty) the Maps SDK throws on the first map view, so `hasMapsApiKey` makes those composables draw a plain placeholder and `AppDelegate` skips `GMSServices.provideAPIKey`. Location permission on iOS goes through the app's own `core/location/IosLocationPermissionController.kt` (When-In-Use only), shared by the place screens' requester and the Geolocator: compass 1.2.2's iOS controller asks for Always access, crashes on a second authorization change and throws on Restricted, so don't go back to `LocationPermissionController.mobile()`. The NotificationService extension renders a push only when its `recipientId` is the signed-in user, which the app shares through the app group `group.com.juanpablo0612.carpool` (`iosApp/Shared/SignedInUserStore.swift`, written by an Auth listener in `AppDelegate`); other pushes keep the backend's generic text. The app target links with `-ObjC` because nothing references Firebase Analytics' static archive. Camera options are hidden where `rememberIsCameraAvailable()` is false (the Simulator, Macs), since FileKit's camera picker crashes there. Permission prompts and other system strings live in `iosApp/iosApp/InfoPlist.xcstrings`. The privacy manifests are `iosApp/iosApp/PrivacyInfo.xcprivacy` and `iosApp/NotificationService/PrivacyInfo.xcprivacy`: update them when the app starts collecting a new kind of data or calling a required-reason API (UserDefaults is declared for Analytics, which ships no manifest, and for the app group).

## DI (Koin)

`di/` has one file per module: `FirebaseModule` (Auth, Firestore, Storage, Functions, Messaging singletons), `AppStateModule` (`UserSession`, `createLocationPermissionRequester`), one module per feature (`AuthModule`, `RouteModule`, `TripModule`, `NotificationModule`, etc.) plus `SplashModule`/`ProfileModule`/`HomeModule`, and `AppModule` — the `includes(...)` aggregate plus `initKoin`. Singletons for Firebase/repos/data sources, factories for use cases, `koinViewModel<T>()` for ViewModels. Hand-written Koin DSL only — no `koin-annotations`/compiler plugin.

## Key Conventions

- **Naming:** `AuthRepository` (interface), `AuthRepositoryImpl` (impl), `UserDto` (DTO), `User` (model), `LoginUseCase` (verb + UseCase, in `domain/{feature}/usecase/`).
- **Sealed classes over enums** for errors, events, actions and states. The only enum-like exception is a type-safe navigation argument (`MyTripsTab`), since navigation supports enums natively.
- **Localization:** all UI strings via `Res.string.*`/`Res.plurals.*` from `composeResources/values/strings.xml` (Spanish in `values-es/`, in exact key parity — add, rename and remove keys in both). No hardcoded strings. Use `stringResource` in composition and the suspend `getString` outside it (e.g. rendering a push). The iOS-only String Catalogs (`InfoPlist.xcstrings`, `Localizable.xcstrings`) keep the same en/es parity.
- **DTOs default every field**, so a partially-missing Firestore document decodes. Unknown fields in a document are ignored by the gitlive decoder.
- **No compatibility code for old data.** The app is not in production: when a schema changes, change the code to the new shape only — no migrations, backfill scripts or legacy fallbacks.
- **Icons:** local XML vectors in `composeResources/drawable/`, accessed with `vectorResource(Res.drawable.icon_name)`. **`material-icons-extended` is forbidden.** Reuse an existing vector when possible; if a new icon is needed, reference it and tell the user which Material Symbol to download — never invent path data.
- **Input UX:** disable `autoCorrect` for credentials, `KeyboardCapitalization.Words` for names, `ImeAction.Next` between fields, `ImeAction.Done` on the last field.
- **State:** immutable data classes, updated via `MutableStateFlow.update { }`.
- **Responsive layout:** screens must work on a 360dp phone, in landscape, at 2× font scale, with the keyboard open, and on tablets. Read breakpoints only through `rememberWindowLayout()` (`presentation/ui/util/WindowLayout.kt`); the `XxxContent` takes `layout: WindowLayout = rememberWindowLayout()` so previews can force a size. Screen `Scaffold`s pass `contentWindowInsets = ScreenInsets` (system bars + display cutout), top bars `TopBarInsets`, and bars pinned to the bottom pad by `BottomBarInsets`. Before `imePadding()`, call `consumeWindowInsets(padding)`. Cap content with `ContentWidth.form`/`.list`: wrap a lazy list in `CenteredContent(..., gutter = 0.dp)` and add the margin with `contentPadding.plusHorizontal(margin)`, so the list still scrolls edge to edge; centre a scrolling column with `.centeredContent(...)` after `verticalScroll`. Lists of cards are `LazyVerticalGrid(GridCells.Adaptive(ContentWidth.gridCell))` with `FullLineSpan` headers. Use `Modifier.mediaPreviewSize()` for maps and photos, never a fixed height. Use `heightIn` rather than `height` on anything containing text. Give long text in rows `maxLines` + `TextOverflow.Ellipsis` or a `weight`. Anything that can outgrow the window scrolls. Preview layout-sensitive screens with `@ScreenPreviews`.
- **Comments** explain the current code's non-obvious *why*; don't narrate history or reference tickets.

## Tech Stack

Versions are tracked in `gradle/libs.versions.toml` — treat that file as the source of truth.

- Kotlin 2.3.21, Compose Multiplatform 1.10.3, Material3, Material3 Adaptive 1.2.0 + navigation suite
- Firebase Auth, Firestore, Storage, Functions, Messaging, Analytics (Kotlin SDK `dev.gitlive:firebase-*` 2.4.0)
- Koin 4.2.1, AndroidX Navigation Compose 2.9.2, KotlinX Serialization, KotlinX Coroutines 1.10.2
- FileKit 0.14.1, KotlinX DateTime 0.8.0
- Android: compileSdk 37, minSdk 24, targetSdk 37
- iOS: deployment target 16.0 (required by the Maps SDK), Xcode 26.4.1+ (not 26.4.0, whose compiler breaks Firebase 11's `async let` code in Release builds), Swift packages `firebase-ios-sdk` 11.x and `ios-maps-sdk` 11.2.0 (pinned in `project.pbxproj`)
- Backend: Node 22, firebase-functions 7, firebase-admin 13, TypeScript
