# AGENTS.md

This document defines the rules, architecture, conventions, and expectations that any AI agent must follow when working on this repository. The goal is to ensure that all generated code is consistent, maintainable, scalable, and aligned with the project's architecture.

Agents must strictly follow these guidelines when generating, modifying, or refactoring code.

------------------------------------------------------------------------

# 1. Build & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Run tests
./gradlew :composeApp:testDebugUnitTest

# Clean build
./gradlew clean :androidApp:assembleDebug
```

Gradle configuration cache is enabled. On Windows use `gradlew.bat` instead of `./gradlew`.

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

- **Language:** Kotlin 2.3.20
- **Architecture:** Clean Architecture + MVVM
- **UI Framework:** Compose Multiplatform 1.10.3, Material3
- **Platform:** Kotlin Multiplatform (Android active, iOS scaffold exists but inactive)
- **Backend:** Firebase Auth + Firestore + Analytics (`dev.gitlive:firebase-*` 2.4.0)
- **DI:** Koin 4.2.0
- **Navigation:** AndroidX Navigation Compose 2.9.2 with type-safe routes
- **Other:** KotlinX Serialization, KotlinX DateTime 0.7.1, FileKit 0.13.0
- **Android:** compileSdk 36, minSdk 24, targetSdk 36, JVM target 17

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

Hybrid architecture (layers + features) inside `composeApp`:

```
com/juanpablo0612/carpool/
├── core/exception/        # AppException sealed class (domain-safe errors)
├── data/{feature}/        # DTOs, remote data sources, repository implementations
├── domain/{feature}/      # Models, repository interfaces, use cases, validators
├── presentation/{feature}/ # Screens, ViewModels, UiState, Actions
├── presentation/ui/       # Shared components & theme
└── di/                    # Koin modules
```

------------------------------------------------------------------------

# 7. UI Components & Core Infrastructure

Contains shared infrastructure: Result wrappers, dispatchers, shared extensions, base error abstractions. **Generic reusable UI components** are located in `presentation/ui/components/` (e.g., `AuthTextField`).

------------------------------------------------------------------------

# 8. Data Layer

Responsibilities: API communication, data sources, DTOs, mappers, and repository implementations.
Convert external errors into domain-safe errors.

------------------------------------------------------------------------

# 9. Domain Layer

Responsibilities: Domain models, business rules, use cases, and repository interfaces.
Pure Kotlin only. No dependency on frameworks or other layers.

------------------------------------------------------------------------

# 10. Presentation Layer & Modularization

Each screen must contain: `ViewModel`, `UiState`, `Action` sealed class, and the `Screen` composable.
**High Granularity Rule:** Screens must be composed of smaller, reusable private components (e.g., `LoginForm`, `RegisterForm`, `RoleSelectionSection`) defined within the same file or `components` package to improve readability and maintainability.

------------------------------------------------------------------------

# 11. Dependency Injection (Koin)

Modules are defined in `di/Modules.kt`: `authModule`, `routeModule`, `placeModule`, composed into `appModule`.
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
- **Use Cases:** `LoginUseCase` (Verb + UseCase)

------------------------------------------------------------------------

# 16. Error Handling & UI Patterns

- Errors: Represented by sealed classes in the domain (`AppException`).
- **NO SNACKBARS:** Do not use `SnackbarHost` for validation or authentication errors.
- **Inline Errors:** Use `errorMessage` properties in text fields and the `ErrorMessage` component for global screen errors.
- Use `.asStringResource()` extensions to map domain errors to localized strings in the UI.

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

No hardcoded strings. Use `Res.string.*` from `composeResources/values/strings.xml` for all UI text. Spanish translations live in `values-es/`.

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

- Type-safe routes via `@Serializable sealed interface Route` + AndroidX Navigation Compose.
- **ObserveAsEvents:** Use the `ObserveAsEvents` utility to handle one-time side-effects like navigation or showing success messages, triggered by a `SharedFlow` in the ViewModel.

------------------------------------------------------------------------

# 22. Security Guidelines

Validate inputs before sending requests. Never store passwords locally. Do not log sensitive info.

------------------------------------------------------------------------

# 23. Code Quality Standards

Follow SOLID, DRY, and Clean Architecture. Avoid business logic in UI, tight coupling, and hardcoded values.

------------------------------------------------------------------------

# 24. Expected AI Behavior

Strictly follow architecture, naming, and structure. Generated code must be production-ready and placed in the correct feature/layer. No pseudo-code.
