# AGENTS.md

This document defines the rules, architecture, conventions, and expectations that any AI agent must follow when working on this repository. The goal is to ensure that all generated code is consistent, maintainable, scalable, and aligned with the project's architecture.

Agents must strictly follow these guidelines when generating, modifying, or refactoring code.

------------------------------------------------------------------------

# 1. Build & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Clean build
./gradlew clean :androidApp:assembleDebug

# Compile shared code only (no Android SDK required)
./gradlew :composeApp:compileCommonMainKotlinMetadata
```

There is no test command. Android host tests are not enabled and the project has no tests — do not invent or run `./gradlew :composeApp:testDebugUnitTest`; that task does not exist. `composeApp` is a Kotlin Multiplatform **library**, not an application module, so `:composeApp:assembleDebug` is also not a valid task — the installable APK comes from `:androidApp:assembleDebug`. There is also no CI workflow and no lint/detekt/ktlint config — `./gradlew :composeApp:compileCommonMainKotlinMetadata` is the only automated check that exists.

Gradle configuration cache is enabled. On Windows use `gradlew.bat` instead of `./gradlew`.

A fresh clone needs two gitignored files before `:androidApp:assembleDebug` will succeed: `androidApp/google-services.json` (Firebase config) and a root `secrets.properties` with `MAPS_API_KEY` (copy `secrets.properties.example`). See `README.md` for details.

------------------------------------------------------------------------

# 2. Project Overview

EnrutadosEIA is a mobile application designed to organize the informal carpooling system used by university students.
EnrutadosEIA replaces disorganized chat groups with a structured platform where drivers can publish trips and passengers can easily find and reserve seats.

------------------------------------------------------------------------

# 3. Primary Goal

The MVP focuses on validating the product idea.
Drivers to: Create routes, publish trips, manage passengers.
Passengers to: Browse trips, view details, reserve seats.
Prioritize simplicity, reliability, and fast interactions.

------------------------------------------------------------------------

# 4. Technology Stack

Versions are tracked in `gradle/libs.versions.toml` — treat that file as the source of truth.

- **Language:** Kotlin 2.3.21
- **Architecture:** Clean Architecture + MVVM
- **UI Framework:** Compose Multiplatform 1.10.3, Material3
- **Platform:** Kotlin Multiplatform (Android active, iOS scaffold exists but inactive)
- **Backend:** Firebase Auth + Firestore + Analytics (`dev.gitlive:firebase-*` 2.4.0)
- **DI:** Koin 4.2.1 (hand-written DSL modules — no `koin-annotations`/compiler plugin)
- **Navigation:** AndroidX Navigation Compose 2.9.2 with type-safe routes
- **Other:** KotlinX Serialization, KotlinX DateTime 0.8.0, FileKit 0.14.1
- **Android:** compileSdk 37, minSdk 24, targetSdk 37, JVM target 17

------------------------------------------------------------------------

# 5. Architecture Principles

Follow Clean Architecture: **presentation → domain → data**.
- Presentation cannot depend on data.
- Domain cannot depend on data or frameworks (pure Kotlin).
- Data implements domain interfaces.
- Business logic exists only in the domain layer.

------------------------------------------------------------------------

# 6. Project Structure

Two-module project: `androidApp` (Android application shell) + `composeApp` (shared KMP library).

Hybrid architecture (layers + features) inside `composeApp`. Feature packages are singular and identical across `data/`, `domain/`, and `presentation/`: `auth, booking, chat, notification, place, preferences, rating, route, safety, trip, vehicle` (`route` is a driver's published route; `trip` is a bookable trip instance on that route — separate features on purpose).

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
│   └── usecase/              # only where there is real logic (see section 9)
├── presentation/{feature}/  # Screens, ViewModels, UiState, Actions, screen-local errors
│   └── .../components/       # leaf composables for screens over ~250 lines
├── presentation/navigation/ # Route.kt + graph/ (see section 21)
├── presentation/ui/         # components/ used by 2+ features, theme, util/
└── di/                       # one Koin module file per feature (see section 11)
```

Non-feature presentation packages also exist alongside the feature ones: `home`, `onboarding`, `profile`, `roleselector`, `session`, `splash`.

------------------------------------------------------------------------

# 7. UI Components & Core Infrastructure

Contains shared infrastructure: Result wrappers, dispatchers, shared extensions, base error abstractions. **Generic reusable UI components** used by two or more features are located in `presentation/ui/components/` (e.g., `CarpoolTextField`, `CarpoolTopBar`, `ErrorMessage`); date formatting and other cross-feature utilities live in `presentation/ui/util/` (`DateTimeFormatUtils.kt`, `ObserveAsEvents.kt`) rather than a separate `presentation/utils/` package.

------------------------------------------------------------------------

# 8. Data Layer

Responsibilities: API communication, data sources, DTOs, mappers, and repository implementations.
Convert external errors into domain-safe errors.

------------------------------------------------------------------------

# 9. Domain Layer

Responsibilities: Domain models, business rules, use cases, and repository interfaces.
Pure Kotlin only. No dependency on frameworks or other layers.

**Use cases exist only where there is real logic** — orchestration across repositories, derivation, ownership checks, or entity construction with id/timestamp. A plain single-call read or write does not get a use case; the ViewModel injects the repository directly instead. There are 15 use cases today, in `domain/{feature}/usecase/`:
- `booking`: CreateBooking, CheckExistingBooking, GetTripAvailableSeats, GetBookingsForTrip, RejectBooking, ConfirmBooking, CancelBooking
- `place`: CreatePlace, DeletePlace, GetSavedPlaces
- `trip`: GetAvailableTrips
- `chat`: SendMessage
- `notification`: CreateNotification
- `rating`: CreateRating
- `safety`: AddEmergencyContact

Exception worth knowing about: `domain/auth/validation/Validator.kt` is called directly from Login/Register/ForgotPassword ViewModels rather than through a use case. That's fine — it's pure framework-free domain logic with no repository involved. Also, `BookingWithPassenger`, `PassengerSummary`, and `TripSummary` live in `presentation/booking/model/` rather than here, since a ViewModel builds them and only Compose consumes them.

------------------------------------------------------------------------

# 10. Presentation Layer & Modularization

Each screen must contain: `ViewModel`, `UiState`, `Action` sealed class, and the `Screen` composable.
**High Granularity Rule:** Screens must be composed of smaller, reusable components (e.g., `LoginForm`, `RegisterForm`, `RoleSelectionSection`). Any screen over roughly 250 lines keeps its leaf composables in a sibling `components/` package (e.g. `presentation/route/detail/components/`); the stateful `XxxScreen` and stateless `XxxContent` stay together in `XxxScreen.kt`. Composables extracted into `components/` are `internal`, not `public`.

------------------------------------------------------------------------

# 11. Dependency Injection (Koin)

`di/` has one file per module rather than a single aggregate: `FirebaseModule` (the three Firebase SDK singletons), `AppStateModule` (`UserSession`, `createLocationPermissionRequester`), one module per feature (`AuthModule`, `RouteModule`, `PlaceModule`, etc.) plus `SplashModule`/`ProfileModule`/`HomeModule`/`RoleSelectorModule`, and `AppModule` — the `includes(...)` aggregate plus `initKoin`.
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

Represent events from UI to ViewModel. UI calls `viewModel.onAction(Action)`. Use sealed classes (not enums) for Action types.

------------------------------------------------------------------------

# 15. Naming Conventions

- **Interfaces:** `AuthRepository`
- **Implementations:** `AuthRepositoryImpl`
- **DTOs:** `UserDto`
- **Models:** `User`
- **Use Cases:** `LoginUseCase` (Verb + UseCase), placed in `domain/{feature}/usecase/`

------------------------------------------------------------------------

# 16. Error Handling & UI Patterns

- **One rule: repositories fail with `AppException`; presentation owns every error type the UI renders.** Every repository maps raw SDK/Firebase exceptions to a subclass of `core/exception/AppException.kt` (one nested sealed class per feature — `AuthException`, `BookingException`, `TripException`, `RouteException`, `VehicleException`, `PlaceException`, `ChatException`, `RatingException`, `NotificationException`, `SafetyException`). Never return `Result.failure(e)` with the raw exception.
- Firestore-backed features (everything except `AuthException`) can currently only ever produce their `.Unknown` case — the `dev.gitlive` Firestore SDK exposes no exception subtypes to branch on the way `FirebaseAuthException` does. `data/auth/repository/AuthRepositoryImpl.kt` is the only file outside a `datasource/` package that imports `dev.gitlive` at all.
- **NO SNACKBARS:** Do not use `SnackbarHost` for validation, authentication, or any other error/status message.
- **Inline Errors:** Use `errorMessage` properties in text fields and the `ErrorMessage` component for global screen errors.
- Two error-typing styles coexist, both in `presentation/{feature}/` now (no error types live in `domain/` any more):
  - **Sealed class + mapper** (`AuthError`/`AuthErrorMapper.kt`, `BookingError`/`BookingErrorMapper.kt`, `TripError`/`TripErrorMapper.kt`, `RatingError`/`RatingErrorMapper.kt`, `NotificationError`/`NotificationErrorMapper.kt`) — for errors that originate from a repository/use-case failure. A `presentation/{feature}/XxxErrorMapper.kt` holds `Throwable.toXxxError()` and `XxxError.asStringResource()` as extension functions. One deliberate exception: `BookingError.VehicleNotFound` is constructed directly in `RouteDetailPassengerViewModel` for a trip whose vehicle is missing, not via a mapped repository failure.
  - **Self-contained presentation error class** (`AddPlaceError`, `CreateRouteError`, `RegisterVehicleError`, `EditProfileFieldError`, `SafetyContactFieldError`, `HomeError`, `RouteDetailError`, `TripTrackingError`) — for screen-local errors that never touch the domain layer, carrying their own member `.asStringResource()`.

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

No hardcoded strings. Use `Res.string.*` from `composeResources/values/strings.xml` for all UI text. Spanish translations live in `values-es/`, in exact key parity with `values/`. Inside a `@Composable`, resolve with `stringResource(Res.string.x)`. When text must be resolved from a ViewModel — e.g. building a notification title/body that gets persisted as plain text — use the suspend `getString(Res.string.x)` from `org.jetbrains.compose.resources` instead.

------------------------------------------------------------------------

# 19. Iconography & Resources

- **NO `material-icons-extended`:** This library is forbidden due to size and performance.
- **Local XML Vectors:** Use only local XML vectors located in `composeResources/drawable`.
- **Access:** Use `vectorResource(Res.drawable.icon_name)`.
- **Material Symbols:** Prefer rounded or sharp Material Symbols exported as XML.
- **Icon Provisioning:** If the agent uses an icon that is not currently in the project, it must include the reference in the code (e.g., `Res.drawable.new_icon`) and clearly state the name of the required icon in the response so the user can download and add it.

------------------------------------------------------------------------

# 20. Input Usability & IME

- **KeyboardOptions:** Disable `autoCorrect` for credentials (email, password). Use `KeyboardCapitalization.Words` for names.
- **IME Actions:** Use `ImeAction.Next` to move between fields and `ImeAction.Done` to trigger the primary action (Login/Register) from the last field.

------------------------------------------------------------------------

# 21. Navigation & Side-Effects

- Type-safe routes via one flat `@Serializable sealed interface Route` (29 routes) in `presentation/navigation/Route.kt`, plus AndroidX Navigation Compose. `presentation/navigation/graph/` splits the graph into `AuthNavGraph`, `DriverNavGraph`, `PassengerNavGraph`, `RootNavGraph` (Splash/Onboarding/RoleSelector), and `SharedNavGraph` (role-agnostic routes). `Navigation.kt` only assembles the `NavHost` plus session/logout/role-switch wiring.
- **ObserveAsEvents:** lives in `presentation/ui/util/` (it's a utility, not a component). Use it to handle one-time side-effects like navigation or showing success messages, triggered by a `SharedFlow` in the ViewModel.

------------------------------------------------------------------------

# 22. Security Guidelines

Validate inputs before sending requests. Never store passwords locally. Do not log sensitive info.

------------------------------------------------------------------------

# 23. Code Quality Standards

Follow SOLID, DRY, and Clean Architecture. Avoid business logic in UI, tight coupling, and hardcoded values.

------------------------------------------------------------------------

# 24. Expected AI Behavior

Strictly follow architecture, naming, and structure. Generated code must be production-ready and placed in the correct feature/layer. No pseudo-code.
