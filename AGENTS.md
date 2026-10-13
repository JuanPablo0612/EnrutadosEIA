# AGENTS.md

This document defines the rules, architecture, conventions, and expectations that any AI agent must follow when working on this repository. The goal is to ensure that all generated code is consistent, maintainable, scalable, and aligned with the project's architecture.

Agents must strictly follow these guidelines when generating, modifying, or refactoring code. `CLAUDE.md` holds the same rules; keep the two in sync.

------------------------------------------------------------------------

# 1. Build & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Clean build
./gradlew clean :androidApp:assembleDebug

# Unit tests (commonTest on the JVM via Android host tests — no device needed)
./gradlew :composeApp:testAndroidHostTest

# Compile shared code only (no Android SDK required)
./gradlew :composeApp:compileCommonMainKotlinMetadata

# Type-check the iOS Kotlin code (works on any host; linking and running need a Mac)
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Cloud Functions (backend)
npm --prefix functions run lint
npm --prefix functions run build
```

Tests live in `composeApp/src/commonTest` (kotlin-test + kotlinx-coroutines-test) and cover pure domain logic; add tests for new domain logic. There are no UI/instrumented tests. `composeApp` is a Kotlin Multiplatform **library**, not an application module, so `:composeApp:assembleDebug` is not a valid task — the installable APK comes from `:androidApp:assembleDebug`. The iOS app is the Xcode project `iosApp/iosApp.xcodeproj` (scheme `iosApp`, Xcode 26.4.1+, on a Mac); its "Compile Kotlin Framework" phase runs `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`. There is no CI workflow and no Kotlin lint/detekt/ktlint config; build, host tests and the functions lint/build are the automated checks.

Gradle configuration cache is enabled. On Windows use `gradlew.bat` instead of `./gradlew`.

A fresh clone needs two gitignored files before `:androidApp:assembleDebug` will succeed: `androidApp/google-services.json` (Firebase config) and a root `secrets.properties` with `MAPS_API_KEY` (copy `secrets.properties.example`). The iOS build also needs `iosApp/iosApp/GoogleService-Info.plist` (Firebase iOS app, bundle ID `com.juanpablo0612.carpool`), `iosApp/Configuration/Local.xcconfig` with the signing `TEAM_ID` (copy `Local.xcconfig.example`; the bundle and app-group ids belong to the owner's paid team, so push builds need that `TEAM_ID`, and the example shows the overrides any other team uses to build without push), an APNs key uploaded to Firebase Cloud Messaging, and **Maps SDK for iOS** enabled on the same `MAPS_API_KEY`. See `README.md` for details.

------------------------------------------------------------------------

# 2. Project Overview

EnrutadosEIA is a mobile application designed to organize the informal carpooling system used by university students.
EnrutadosEIA replaces disorganized chat groups with a structured platform where drivers can publish trips and passengers can easily find and reserve seats. There are **no user roles**: every account can search and book, and anyone with a registered vehicle can publish trips, all from one navigation.

------------------------------------------------------------------------

# 3. Primary Goal

The MVP focuses on validating the product idea.
Driving: publish one-off trips (optionally saving them as frequent routes), publish a week of a recurring route, manage seat requests and passengers.
Riding: find trips that pass near your origin and destination, view details, request seats, follow the trip, chat and rate.
Prioritize simplicity, reliability, and fast interactions.

------------------------------------------------------------------------

# 4. Technology Stack

Versions are tracked in `gradle/libs.versions.toml` — treat that file as the source of truth.

- **Language:** Kotlin 2.3.21
- **Architecture:** Clean Architecture + MVVM
- **UI Framework:** Compose Multiplatform 1.10.3, Material3, Material3 Adaptive 1.2.0 + navigation suite
- **Platform:** Kotlin Multiplatform — Android and iOS (deployment target 16.0, required by the Maps SDK; Xcode 26.4.1+, not 26.4.0, whose compiler breaks Firebase 11's `async let` code in Release builds)
- **Backend:** Firebase Auth, Firestore, Storage, Functions, Messaging, Analytics (`dev.gitlive:firebase-*` 2.4.0) + Cloud Functions in `functions/` (TypeScript, Node 22, firebase-functions 7, firebase-admin 13)
- **DI:** Koin 4.2.1 (hand-written DSL modules — no `koin-annotations`/compiler plugin)
- **Navigation:** AndroidX Navigation Compose 2.9.2 with type-safe routes
- **Other:** KotlinX Serialization, KotlinX Coroutines 1.10.2, KotlinX DateTime 0.8.0, FileKit 0.14.1
- **Android:** compileSdk 37, minSdk 24, targetSdk 37, JVM target 17
- **iOS:** Swift packages in the Xcode project (no CocoaPods): `firebase-ios-sdk` 11.x — the version the gitlive 2.x cinterop is built against, so don't move to 12.x without upgrading gitlive — and `ios-maps-sdk` 11.2.0

------------------------------------------------------------------------

# 5. Architecture Principles

Follow Clean Architecture: **presentation → domain → data**.
- Presentation cannot depend on data.
- Domain cannot depend on data or frameworks (pure Kotlin).
- Data implements domain interfaces.
- Business logic exists only in the domain layer.

------------------------------------------------------------------------

# 6. Project Structure

Two-module project: `androidApp` (Android application shell) + `composeApp` (shared KMP library), plus the Xcode project `iosApp/` (iOS application shell).

Hybrid architecture (layers + features) inside `composeApp`. Feature packages are singular and identical across `data/`, `domain/`, and `presentation/`: `auth, booking, chat, notification, place, preferences, rating, route, trip, vehicle` (`route` is a driver's saved route template; `trip` is a bookable trip with date, vehicle and seats — separate features on purpose; a one-off trip has `routeId == ""`).

```
com/juanpablo0612/carpool/
├── core/config/            # FeatureFlags, BackendConfig, generated BuildKonfig
├── core/exception/         # AppException sealed class (domain-safe errors)
├── data/{feature}/
│   ├── model/               # DTOs (absent for `preferences`, which has no Firestore DTO)
│   ├── datasource/           # owns every Firebase/DataStore call; throws
│   └── repository/           # DTO→domain mapping, catches and returns Result<T>
├── domain/{feature}/
│   ├── model/                # domain models
│   ├── repository/           # repository interfaces
│   ├── usecase/              # only where there is real logic (see section 9)
│   └── validation/           # pure validators (auth/Validator, trip/TripDraftValidator)
├── presentation/{feature}/  # Screens, ViewModels, UiState, Actions, screen-local errors
│   └── .../components/       # leaf composables for screens over ~250 lines
├── presentation/navigation/ # Route.kt + graph/ (see section 21)
├── presentation/ui/         # components/ used by 2+ features, theme, util/
└── di/                       # one Koin module file per feature (see section 11)
```

Non-feature presentation packages also exist alongside the feature ones: `home` (Inicio), `mytrips` (Mis viajes tab host), `onboarding`, `profile`, `session`, `splash`. `presentation/place/stops/` holds the stop editor shared by route create/edit and trip publishing (`StopsDraft`, `SelectionTarget`, `stopsEditorItems`, `StopSelectionHost`). Android-only push code lives in `composeApp/src/androidMain/.../push/`, iOS push glue in `composeApp/src/iosMain/.../push/`; both parse the backend's envelope with the shared `data/notification/model/PushPayload`.

**iOS shell (`iosApp/`).** A SwiftUI app around `MainViewController()`. `AppDelegate.swift` configures Firebase and the Maps SDK, then calls `IosApp.start` (iosMain; Koin + `PushTokenSync`, the counterpart of `CarpoolApplication`). `AppDelegate+Notifications.swift` hands the APNs token to FCM (swizzling is off) and forwards foreground pushes and taps to `IosPushNotifications`. Kotlin can't import the Swift-package SDKs, so the Google Maps views are Swift (`iosApp/iosApp/Maps/`) implementing the Kotlin interfaces in `NativeMapViews.kt`, embedded by the iOS `MapPreview`/`MapRoutePreview` with `UIKitView`. The `NotificationService` app extension rewrites each push's text on the device (`NotificationText.swift` mirrors `resolveNotificationText`; strings in `iosApp/Shared/Localizable.xcstrings`). Without a Maps key (`BuildKonfig.MAPS_API_KEY` empty) the Maps SDK throws on the first map view, so `hasMapsApiKey` makes those composables draw a plain placeholder and `AppDelegate` skips `GMSServices.provideAPIKey`. iOS location permission goes through the app's own `core/location/IosLocationPermissionController.kt` (When-In-Use only), shared by the place screens' requester and the Geolocator; compass 1.2.2's iOS controller asks for Always access, crashes on a second authorization change and throws on Restricted, so don't go back to `LocationPermissionController.mobile()`. The extension renders a push only when its `recipientId` is the signed-in user, shared through the app group `group.com.juanpablo0612.carpool` (`iosApp/Shared/SignedInUserStore.swift`, written by an Auth listener in `AppDelegate`); other pushes keep the backend's generic text. The app target links with `-ObjC` because nothing references Firebase Analytics' static archive. Camera options are hidden where `rememberIsCameraAvailable()` is false (the Simulator, Macs), since FileKit's camera picker crashes there. The privacy manifests are `iosApp/iosApp/PrivacyInfo.xcprivacy` and `iosApp/NotificationService/PrivacyInfo.xcprivacy`: update them when the app starts collecting a new kind of data or calling a required-reason API (UserDefaults is declared for Analytics, which ships no manifest, and for the app group).

------------------------------------------------------------------------

# 7. UI Components & Core Infrastructure

Contains shared infrastructure: Result wrappers, dispatchers, shared extensions, base error abstractions. **Generic reusable UI components** used by two or more features are located in `presentation/ui/components/` (e.g., `CarpoolTextField`, `CarpoolTopBar`, `ErrorMessage`); date formatting and other cross-feature utilities live in `presentation/ui/util/` (`DateTimeFormatUtils.kt`, `ObserveAsEvents.kt`) rather than a separate `presentation/utils/` package.

------------------------------------------------------------------------

# 8. Data Layer

Responsibilities: API communication, data sources, DTOs, mappers, and repository implementations.
Convert external errors into domain-safe errors. Every DTO field has a default so a partially-missing document decodes.

**No compatibility code for old data.** The app is not in production: when a schema changes, change the code to the new shape only — no migrations, backfill scripts or legacy fallbacks.

------------------------------------------------------------------------

# 9. Domain Layer

Responsibilities: Domain models, business rules, use cases, and repository interfaces.
Pure Kotlin only. No dependency on frameworks or other layers.

**Use cases exist only where there is real logic** — orchestration across repositories, derivation, ownership checks, validation, or entity construction. A plain single-call read or write does not get a use case; the ViewModel injects the repository directly instead. There are 19 use cases today, in `domain/{feature}/usecase/`:
- `booking`: CreateBooking, CheckExistingBooking, GetTripAvailableSeats, GetBookingsForTrip, RejectBooking, ConfirmBooking, CancelBooking
- `place`: CreatePlace, DeletePlace, GetSavedPlaces
- `route`: DuplicateRoute
- `trip`: GetAvailableTrips, MatchTrips, GenerateRecurringTripSlots, PublishTrip, PublishRecurringTrips, UpdateTrip
- `chat`: SendMessage
- `rating`: CreateRating

Pure validators (`domain/auth/validation/Validator.kt`, `domain/trip/validation/TripDraftValidator.kt`) are called directly. Time-dependent domain logic takes `now` and the time zone as parameters so it stays deterministic and testable. Also, `BookingWithPassenger`, `PassengerSummary`, and `TripSummary` live in `presentation/booking/model/` rather than here, since a ViewModel builds them and only Compose consumes them.

------------------------------------------------------------------------

# 10. Presentation Layer & Modularization

Each screen must contain: `ViewModel`, `UiState`, `Action` sealed class, and the `Screen` composable.
**High Granularity Rule:** Screens must be composed of smaller, reusable components (e.g., `LoginForm`, `RegisterStep1`, `WeekSlotRow`). Any screen over roughly 250 lines keeps its leaf composables in a sibling `components/` package (e.g. `presentation/route/detail/components/`); the stateful `XxxScreen` and stateless `XxxContent` stay together in `XxxScreen.kt`. Composables extracted into `components/` are `internal`, not `public`.

------------------------------------------------------------------------

# 11. Dependency Injection (Koin)

`di/` has one file per module rather than a single aggregate: `FirebaseModule` (Auth, Firestore, Storage, Functions and Messaging singletons), `AppStateModule` (`UserSession`, `createLocationPermissionRequester`), one module per feature (`AuthModule`, `RouteModule`, `TripModule`, `NotificationModule`, etc.) plus `SplashModule`/`ProfileModule`/`HomeModule`, and `AppModule` — the `includes(...)` aggregate plus `initKoin`.
- **Singletons:** Firebase clients, repository implementations.
- **Factories:** Use cases.
- **ViewModels:** Injected with `koinViewModel<T>()`.

------------------------------------------------------------------------

# 12. ViewModel Rules

One ViewModel per screen. Exposes `StateFlow<UiState>` and `SharedFlow<Event>`. UI calls `onAction(Action)`. Use domain use cases only; map domain errors to UI state. ViewModels must not contain business logic.

------------------------------------------------------------------------

# 13. UI State

Immutable state classes (e.g., `LoginUiState`). Update state immutably via `MutableStateFlow.update { }` and observe using `StateFlow`.

------------------------------------------------------------------------

# 14. Actions

Represent events from UI to ViewModel. UI calls `viewModel.onAction(Action)`. Use sealed classes (not enums) for Action, Event, error and state types. The only enum is a type-safe navigation argument (`MyTripsTab`), because navigation supports enums natively.

------------------------------------------------------------------------

# 15. Naming Conventions

- **Interfaces:** `AuthRepository`
- **Implementations:** `AuthRepositoryImpl`
- **DTOs:** `UserDto`
- **Models:** `User`
- **Use Cases:** `LoginUseCase` (Verb + UseCase), placed in `domain/{feature}/usecase/`

------------------------------------------------------------------------

# 16. Error Handling & UI Patterns

- **One rule: repositories fail with `AppException`; presentation owns every error type the UI renders.** Every repository maps raw SDK/Firebase exceptions to a subclass of `core/exception/AppException.kt` (one nested sealed class per feature — `AuthException`, `BookingException`, `TripException`, `RouteException`, `VehicleException`, `PlaceException`, `ChatException`, `RatingException`, `NotificationException`). Never return `Result.failure(e)` with the raw exception.
- Firestore-backed repositories mostly produce `.Unknown` — the `dev.gitlive` Firestore SDK exposes no exception subtypes to branch on the way `FirebaseAuthException` does. `TripException` also carries use-case outcomes (`Invalid(errors)`, `NotAuthenticated`, `VehicleNotFound`, `RouteSavedTripFailed`).
- **Forms** show validation problems next to each field when the primary button is tapped, instead of silently disabling the button.
- **NO SNACKBARS:** Do not use `SnackbarHost` for validation, authentication, or any other error/status message.
- **Inline Errors:** Use `errorMessage` properties in text fields and the `ErrorMessage` component for global screen errors.
- Two error-typing styles coexist, both in `presentation/{feature}/` now (no error types live in `domain/` any more):
  - **Sealed class + mapper** (`AuthError`/`AuthErrorMapper.kt`, `BookingError`/`BookingErrorMapper.kt`, `TripError`/`TripErrorMapper.kt`, `RatingError`/`RatingErrorMapper.kt`, `NotificationError`/`NotificationErrorMapper.kt`) — for errors that originate from a repository/use-case failure. A `presentation/{feature}/XxxErrorMapper.kt` holds `Throwable.toXxxError()` and `XxxError.asStringResource()` as extension functions. One deliberate exception: `BookingError.VehicleNotFound` is constructed directly in `RouteDetailPassengerViewModel` for a trip whose vehicle is missing, not via a mapped repository failure.
  - **Self-contained presentation error class** (`AddPlaceError`, `CreateRouteError`, `RegisterVehicleError`, `EditProfileFieldError`, `HomeError`, `RouteDetailError`, `TripTrackingError`) — for screen-local errors that never touch the domain layer, carrying their own member `.asStringResource()`.

------------------------------------------------------------------------

# 17. Previews and Content Separation

To enable Compose Previews and maintain a clean separation of concerns:
- Each screen must be split into two main functions:
    1. **Screen Function:** (e.g., `LoginScreen`) Handles lifecycle, ViewModel interaction, state collection, and event observation.
    2. **Content Function:** (e.g., `LoginContent`) A stateless or state-receiver composable that only takes the necessary state and event callbacks. It **must not** reference the ViewModel.
- All screens must include a `@Preview` composable that uses the `Content` function wrapped in the project's theme (e.g., `CarpoolTheme`).
- This separation facilitates testing and allows the use of the Compose Preview tool without requiring complex ViewModel injection.

------------------------------------------------------------------------

# 18. Localization

No hardcoded strings. Use `Res.string.*`/`Res.plurals.*` from `composeResources/values/strings.xml` for all UI text. Spanish translations live in `values-es/`, in exact key parity with `values/` (add, rename and remove keys in both). Inside a `@Composable`, resolve with `stringResource(Res.string.x)`; outside composition (e.g. rendering a push notification) use the suspend `getString(Res.string.x)`. Never persist localized text to Firestore: notifications are stored as a type plus params and rendered on the reader's device by `resolveNotificationText` (and, for iOS pushes, by its Swift mirror in the NotificationService extension). A new or changed notification kind touches `NotificationType.kt`/`NotificationText.kt`/`strings.xml`, `functions/src/model.ts`, `iosApp/NotificationService/NotificationText.swift` and `iosApp/Shared/Localizable.xcstrings` (same keys and texts as composeResources). iOS system strings such as permission prompts live in `iosApp/iosApp/InfoPlist.xcstrings`. The iOS String Catalogs keep the same en/es parity.

------------------------------------------------------------------------

# 19. Iconography & Resources

- **NO `material-icons-extended`:** This library is forbidden due to size and performance.
- **Local XML Vectors:** Use only local XML vectors located in `composeResources/drawable`.
- **Access:** Use `vectorResource(Res.drawable.icon_name)`.
- **Material Symbols:** Prefer rounded or sharp Material Symbols exported as XML.
- **Icon Provisioning:** Reuse an existing vector when possible. If an icon that is not in the project is needed, reference it in code (e.g., `Res.drawable.new_icon`) and clearly state the required Material Symbol so the user can download it. Never invent vector path data.

------------------------------------------------------------------------

# 20. Input Usability, IME & Responsive Layout

- **KeyboardOptions:** Disable `autoCorrect` for credentials (email, password). Use `KeyboardCapitalization.Words` for names.
- **IME Actions:** Use `ImeAction.Next` to move between fields and `ImeAction.Done` to trigger the primary action (Login/Register) from the last field.
- **Responsive layout:** screens must work on a 360dp phone, in landscape, at 2× font scale, with the keyboard open, and on tablets. Read breakpoints only through `rememberWindowLayout()` (`presentation/ui/util/WindowLayout.kt`); the `XxxContent` takes `layout: WindowLayout = rememberWindowLayout()` so previews can force a size. Screen `Scaffold`s pass `contentWindowInsets = ScreenInsets` (system bars + display cutout), top bars `TopBarInsets`, and bars pinned to the bottom pad by `BottomBarInsets`. Before `imePadding()`, call `consumeWindowInsets(padding)`. Cap content with `ContentWidth.form`/`.list`: wrap a lazy list in `CenteredContent(..., gutter = 0.dp)` and add the margin with `contentPadding.plusHorizontal(margin)`, so the list still scrolls edge to edge; centre a scrolling column with `.centeredContent(...)` after `verticalScroll`. Lists of cards are `LazyVerticalGrid(GridCells.Adaptive(ContentWidth.gridCell))` with `FullLineSpan` headers. Use `Modifier.mediaPreviewSize()` for maps and photos, never a fixed height. Use `heightIn` rather than `height` on anything containing text. Give long text in rows `maxLines` + `TextOverflow.Ellipsis` or a `weight`. Anything that can outgrow the window scrolls. Preview layout-sensitive screens with `@ScreenPreviews`.

------------------------------------------------------------------------

# 21. Navigation & Side-Effects

- Type-safe routes via one flat `@Serializable sealed interface Route` (31 routes) in `presentation/navigation/Route.kt`, plus AndroidX Navigation Compose.
- One set of tabs for everyone: **Inicio · Buscar · Mis viajes · Perfil**, shown by `NavigationSuiteScaffoldLayout` as the bottom bar on phones and `NavigationRailBar` on medium/expanded windows. Switch tabs only with `NavHostController.navigateToTopLevel` (saves/restores each tab's stack, Home is the root); never push a tab destination.
- `presentation/navigation/graph/`: `RootNavGraph` (Splash, Onboarding), `AuthNavGraph` (Entry — where signed-out users start, asking whether they have an account — then Login, Register, ForgotPassword, EmailVerification), `MainNavGraph` (Home, SearchTrips, MyTrips, TripDetailPassenger), `DriverNavGraph` (screens for trips you drive, reachable by everyone) and `SharedNavGraph` (profile, places, notifications, chat, tracking, rating). `Navigation.kt` assembles the `NavHost`, bottom bar and badges, logout, session reload after process death, and push-tap handling (`PendingDeepLinks`).
- Notification deep links are plain strings built/parsed in `NotificationDeepLink.kt` (`forNotification`, `toRouteOrNull`) and opened with `navigateToNotificationDeepLink`.
- **ObserveAsEvents:** lives in `presentation/ui/util/` (it's a utility, not a component). Use it to handle one-time side-effects like navigation or showing success messages, triggered by a `SharedFlow` in the ViewModel.

------------------------------------------------------------------------

# 22. Backend (Cloud Functions) & Security

`functions/` owns these writes; the app must never do them itself: in-app notifications and FCM pushes (triggers on bookings, trips and chat messages; data-only on Android, an `apns` alert with a generic loc-key text and `mutable-content` on iOS), the trip-cancellation cascade, the rating aggregate (`users/{uid}.ratingSum`/`ratingCount`), and account deletion (the `deleteAccount` callable plus an `auth.user().onDelete` safety net). Functions code follows eslint-config-google (JSDoc on every function, max 80 columns) and deploys to `us-central1` (matching the Firestore location and `BackendConfig.FUNCTIONS_REGION`). Firestore rules must stay the enforcement counterpart of every client write; validate rule changes with `firebase deploy --only firestore:rules --dry-run`.

Validate inputs before sending requests (client) and in the rules (server). Never store passwords locally. Do not log sensitive info.

------------------------------------------------------------------------

# 23. Code Quality Standards

Follow SOLID, DRY, and Clean Architecture. Avoid business logic in UI, tight coupling, and hardcoded values. Comments explain the current code's non-obvious *why*; don't narrate history or reference tickets.

------------------------------------------------------------------------

# 24. Expected AI Behavior

Strictly follow architecture, naming, and structure. Generated code must be production-ready and placed in the correct feature/layer. No pseudo-code.
