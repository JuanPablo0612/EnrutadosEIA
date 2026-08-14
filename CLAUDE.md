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

There is no test command — android host tests are not enabled (`composeApp`'s `commonTest` source set exists but isn't wired to a runnable target) and the project has no tests. Don't add `./gradlew :composeApp:testDebugUnitTest` to docs or scripts; it does not exist. `composeApp` is a Kotlin Multiplatform **library** module, not an application — `:composeApp:assembleDebug` is not a valid task; the installable APK is built via `:androidApp:assembleDebug`.

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

**Clean Architecture + MVVM**, organized as hybrid layers + features:

```
com/juanpablo0612/carpool/
├── core/exception/        # AppException sealed class (domain-safe errors)
├── data/{feature}/        # DTOs, remote data sources, repository implementations
├── domain/{feature}/      # Models, repository interfaces, use cases, validators
├── presentation/{feature}/ # Screens, ViewModels, UiState, Actions
├── presentation/ui/       # Shared components & theme
└── di/                    # Koin modules
```

**Dependency rule:** presentation → domain ← data. Domain is pure Kotlin with no framework imports.

## Key Patterns

**Screen structure** — every screen has two composables:
1. `XxxScreen` — injects ViewModel, collects state, handles navigation side-effects
2. `XxxContent` — stateless, receives state + callbacks, supports `@Preview`

**ViewModel** — one per screen. Exposes `StateFlow<UiState>` + `SharedFlow<Event>`. UI calls `onAction(Action)`. No business logic in ViewModels.

**Use cases** — single-responsibility, stateless. Injected as Koin factories.

**Error handling** — every repository maps raw SDK/Firebase exceptions to a domain-safe subclass of `core/exception/AppException.kt` at the data-layer boundary (one nested sealed class per feature: `AuthException`, `BookingException`, `TripException`, `RouteException`, `VehicleException`, `PlaceException`, `ChatException`, `RatingException`, `NotificationException`, `SafetyException`) — `Result.failure(e)` with a raw exception is never returned. Two error-typing styles coexist depending on where the error is produced:
  - **Domain-pure sealed class + presentation mapper** (`AuthError`/`AuthErrorMapper.kt`, `BookingError`/`BookingErrorMapper.kt`, `TripError`/`TripErrorMapper.kt`, `RatingError`/`RatingErrorMapper.kt`) — for errors that originate from a repository/use-case failure. The domain class stays framework-free; a `presentation/{feature}/XxxErrorMapper.kt` holds `Throwable.toXxxError()` and `XxxError.asStringResource()` as extension functions.
  - **Self-contained presentation error class** (`AddPlaceError`, `CreateRouteError`, `RegisterVehicleError`, `EditProfileFieldError`, `SafetyContactFieldError`, `HomeError`) — for screen-local validation errors that never touch the domain layer. These live in `presentation/{feature}/` and carry their own member `asStringResource()`.

  Either way, errors reach the UI as `ErrorMessage`/`ErrorState` components or inline text-field errors — **never SnackBars, never a raw `throwable.message`.**

**Navigation** — type-safe routes via `@Serializable sealed interface Route` + AndroidX Navigation Compose. One-time events use `ObserveAsEvents` utility with `SharedFlow`.

## DI (Koin)

Modules defined in `di/Modules.kt`: `authModule`, `routeModule`, `placeModule`, composed into `appModule`. Singletons for Firebase/repos, factories for use cases, `koinViewModel<T>()` for ViewModels. Hand-written Koin DSL only — the project does not use `koin-annotations` or the `koinCompose` compiler plugin (`@Single`/`@Factory`/`@KoinViewModel`), so don't add those dependencies back without actually adopting the annotation-based style everywhere.

## Key Conventions

- **Naming:** `AuthRepository` (interface), `AuthRepositoryImpl` (impl), `UserDto` (DTO), `User` (model), `LoginUseCase` (verb + UseCase)
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
