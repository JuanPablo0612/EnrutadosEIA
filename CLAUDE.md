# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

EnrutadosEIA — a Kotlin Multiplatform / Compose Multiplatform carpooling app for university students. Drivers publish trips, passengers find and reserve seats. Currently targeting Android (iOS scaffold exists but is not active).

## Build & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Clean build
./gradlew clean :androidApp:assembleDebug

# Compile shared code only (no Android SDK required)
./gradlew :composeApp:compileCommonMainKotlinMetadata
```

There is no test command — android host tests are not enabled (`composeApp`'s `commonTest` source set exists but isn't wired to a runnable target) and the project has no tests. Don't add `./gradlew :composeApp:testDebugUnitTest` to docs or scripts; it does not exist. `composeApp` is a Kotlin Multiplatform **library** module, not an application — `:composeApp:assembleDebug` is not a valid task; the installable APK is built via `:androidApp:assembleDebug`. There is also no CI workflow and no lint/detekt/ktlint config — `./gradlew :composeApp:compileCommonMainKotlinMetadata` is the only automated check available.

Two-module project: `androidApp` (Android application shell) + `composeApp` (shared KMP library). Gradle configuration cache is enabled. JVM target is 17.

### First-time setup

A fresh clone is missing two gitignored files the Android build needs:

1. **`androidApp/google-services.json`** — Firebase config for the `com.juanpablo0612.carpool` app. Download it from the Firebase console (Project settings → your Android app) and place it at that exact path. Without it, the `googleServices` Gradle plugin fails the build.
2. **`secrets.properties`** at the repo root — holds `MAPS_API_KEY`, consumed by `androidApp/build.gradle.kts` and injected into the manifest as a placeholder. Copy `secrets.properties.example` to `secrets.properties` and fill in a real Google Maps API key. Without this file (or with a blank key), the build still succeeds but the key resolves to an empty string and every map screen renders blank.

### Firestore/Storage rules, indexes, and data backfills

`firestore.rules`, `firestore.indexes.json`, and `storage.rules` at the repo root are deployed with the Firebase CLI (`firebase deploy --only firestore:rules,firestore:indexes,storage`) — see `README.md` for the full setup. `.firebaserc` and `firebase.json` are checked in and already point at project `enrutados-eia`, so `firebase use enrutados-eia` is all that's needed before deploying (no `firebase init` required). Storage has no `:rules` sub-target — `--only storage:rules` fails; use plain `--only storage`. Before deploying rules to a database with real data, backfill two Firestore fields that predate them and silently misbehave on existing documents:

- **`places.ownerId`** defaults to `""` on documents written before the owner field existed, which matches no signed-in user's UID — those places become invisible to their own owner once the rules are enforced.
- **`trips.confirmedSeats`** defaults to `0` on trips that already had confirmed passengers, which over-reports availability and allows overbooking until the counter is next touched by a booking transition. Backfill it as `count(bookings where tripId == trip.id and status == 'CONFIRMED')`.

## Architecture

**Clean Architecture + MVVM**, organized as hybrid layers + features. Feature packages are singular and identical across `data/`, `domain/`, and `presentation/`: `auth, booking, chat, notification, place, preferences, rating, route, safety, trip, vehicle` (`route` is a driver's published route; `trip` is a bookable trip instance on that route — they are separate features on purpose).

```
com/juanpablo0612/carpool/
├── core/exception/         # AppException sealed class (domain-safe errors)
├── data/{feature}/
│   ├── model/               # DTOs (absent for `preferences`, which has no Firestore DTO)
│   ├── datasource/           # owns every Firebase/DataStore call; throws
│   └── repository/           # DTO→domain mapping, catches and returns Result<T>
├── domain/{feature}/
│   ├── model/                # domain models
│   ├── repository/           # repository interfaces
│   └── usecase/              # only where there is real logic (see below)
├── presentation/{feature}/  # Screens, ViewModels, UiState, Actions, screen-local errors
│   └── .../components/       # leaf composables for screens over ~250 lines
├── presentation/navigation/ # Route.kt + graph/ (see Navigation below)
├── presentation/ui/         # components/ used by 2+ features, theme, util/
└── di/                       # one Koin module file per feature (see DI below)
```

Non-feature presentation packages also exist alongside the feature ones: `home`, `onboarding`, `profile`, `roleselector`, `session`, `splash`.

**Dependency rule:** presentation → domain ← data. Domain is pure Kotlin with no framework imports.

## Key Patterns

**Screen structure** — every screen has two composables:
1. `XxxScreen` — injects ViewModel, collects state, handles navigation side-effects
2. `XxxContent` — stateless, receives state + callbacks, supports `@Preview`

**ViewModel** — one per screen. Exposes `StateFlow<UiState>` + `SharedFlow<Event>`. UI calls `onAction(Action)`. No business logic in ViewModels. `BookingWithPassenger`, `PassengerSummary`, and `TripSummary` live in `presentation/booking/model/` rather than `domain/` — a ViewModel builds them and only Compose consumes them, so there's no reason to keep them framework-free.

**Use cases** — 15 total, kept only where there is real logic: orchestration across repositories, derivation, ownership checks, or entity construction with id/timestamp (`booking`: CreateBooking, CheckExistingBooking, GetTripAvailableSeats, GetBookingsForTrip, RejectBooking, ConfirmBooking, CancelBooking · `place`: CreatePlace, DeletePlace, GetSavedPlaces · `trip`: GetAvailableTrips · `chat`: SendMessage · `notification`: CreateNotification · `rating`: CreateRating · `safety`: AddEmergencyContact). For a plain single-call read or write, the ViewModel injects the repository directly instead of wrapping it in a use case. `domain/auth/validation/Validator.kt` is one exception worth knowing about: it's called directly from Login/Register/ForgotPassword ViewModels rather than through a use case — that's fine, since it's pure framework-free domain logic with no repository involved.

**Error handling** — one rule: **repositories fail with `AppException`; presentation owns every error type the UI renders.** Every repository maps raw SDK/Firebase exceptions to a domain-safe subclass of `core/exception/AppException.kt` at the data-layer boundary (one nested sealed class per feature: `AuthException`, `BookingException`, `TripException`, `RouteException`, `VehicleException`, `PlaceException`, `ChatException`, `RatingException`, `NotificationException`, `SafetyException`) — `Result.failure(e)` with a raw exception is never returned. Firestore-backed features (everything except `AuthException`) can currently only ever produce their `.Unknown` case: the `dev.gitlive` Firestore SDK exposes no exception subtypes to branch on the way `FirebaseAuthException` does, so `data/auth/repository/AuthRepositoryImpl.kt` is the only file outside a `datasource/` package that imports `dev.gitlive` at all. Two error-typing styles coexist in `presentation/{feature}/`, both purely presentation-layer now:
  - **Sealed class + mapper** (`AuthError`/`AuthErrorMapper.kt`, `BookingError`/`BookingErrorMapper.kt`, `TripError`/`TripErrorMapper.kt`, `RatingError`/`RatingErrorMapper.kt`, `NotificationError`/`NotificationErrorMapper.kt`) — for errors that originate from a repository/use-case failure. A `presentation/{feature}/XxxErrorMapper.kt` holds `Throwable.toXxxError()` and `XxxError.asStringResource()` as extension functions. One deliberate exception: `BookingError.VehicleNotFound` is constructed directly in `RouteDetailPassengerViewModel` for a trip whose vehicle is missing, not via a mapped repository failure.
  - **Self-contained presentation error class** (`AddPlaceError`, `CreateRouteError`, `RegisterVehicleError`, `EditProfileFieldError`, `SafetyContactFieldError`, `HomeError`, `RouteDetailError`, `TripTrackingError`) — for screen-local errors that never touch the domain layer. These carry their own member `asStringResource()`.

  Either way, errors reach the UI as `ErrorMessage`/`ErrorState` components or inline text-field errors — **never SnackBars, never a raw `throwable.message`.**

**Navigation** — type-safe routes via one flat `@Serializable sealed interface Route` (29 routes) in `presentation/navigation/Route.kt`. `presentation/navigation/graph/` splits the graph into `AuthNavGraph`, `DriverNavGraph`, `PassengerNavGraph`, `RootNavGraph` (Splash/Onboarding/RoleSelector), and `SharedNavGraph` (role-agnostic routes). `Navigation.kt` only assembles the `NavHost` plus session/logout/role-switch wiring. One-time events use the `ObserveAsEvents` utility (in `presentation/ui/util/`) with `SharedFlow`.

## DI (Koin)

`di/` has one file per module instead of a single aggregate: `FirebaseModule` (the three Firebase SDK singletons), `AppStateModule` (`UserSession`, `createLocationPermissionRequester`), one module per feature (`AuthModule`, `RouteModule`, `PlaceModule`, etc.) plus `SplashModule`/`ProfileModule`/`HomeModule`/`RoleSelectorModule`, and `AppModule` — the `includes(...)` aggregate plus `initKoin`. Singletons for Firebase/repos, factories for use cases, `koinViewModel<T>()` for ViewModels. Hand-written Koin DSL only — the project does not use `koin-annotations` or the `koinCompose` compiler plugin (`@Single`/`@Factory`/`@KoinViewModel`), so don't add those dependencies back without actually adopting the annotation-based style everywhere.

## Key Conventions

- **Naming:** `AuthRepository` (interface), `AuthRepositoryImpl` (impl), `UserDto` (DTO), `User` (model), `LoginUseCase` (verb + UseCase, in `domain/{feature}/usecase/`)
- **Localization:** all UI strings via `Res.string.*` from `composeResources/values/strings.xml` (Spanish in `values-es/`, kept in exact key parity with `values/`). No hardcoded strings. Inside a `@Composable`, resolve with `stringResource(Res.string.x)`; when text must be resolved **outside** composition — e.g. a notification title/body that gets persisted to Firestore as plain text from a ViewModel — use the suspend `getString(Res.string.x)` from `org.jetbrains.compose.resources` instead.
- **DTOs default every field.** Every field in a `data/{feature}/model/XxxDto.kt` has a default value, so a partially-missing Firestore document decodes instead of throwing (which otherwise surfaces as a generic failure and, for `UserDto` specifically, previously caused a login loop).
- **Icons:** local XML vectors in `composeResources/drawable/`. Access via `vectorResource(Res.drawable.icon_name)`. **`material-icons-extended` is forbidden.** If a new icon is needed, reference it in code and tell the user which icon to download.
- **Input UX:** disable `autoCorrect` for credentials, use `KeyboardCapitalization.Words` for names, `ImeAction.Next` between fields, `ImeAction.Done` on last field.
- **State:** immutable data classes, updated via `MutableStateFlow.update { }`.

## Tech Stack

Versions below are tracked in `gradle/libs.versions.toml` — treat that file as the source of truth and re-check it if this list goes stale.

- Kotlin 2.3.21, Compose Multiplatform 1.10.3, Material3
- Firebase Auth + Firestore + Analytics (Kotlin SDK `dev.gitlive:firebase-*` 2.4.0)
- Koin 4.2.1, AndroidX Navigation Compose 2.9.2, KotlinX Serialization
- FileKit 0.14.1, KotlinX DateTime 0.8.0
- Android: compileSdk 37, minSdk 24, targetSdk 37
