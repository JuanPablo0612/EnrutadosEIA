# AGENTS.md

This document defines the rules, architecture, conventions, and expectations that any AI agent must follow when working on this repository. The goal is to ensure that all generated code is consistent, maintainable, scalable, and aligned with the project's architecture.

Agents must strictly follow these guidelines when generating, modifying, or refactoring code.

------------------------------------------------------------------------

# 1. Project Overview

CampusRide is a mobile application designed to organize the informal carpooling system used by university students.
CampusRide replaces disorganized chat groups with a structured platform where drivers can publish trips and passengers can easily find and reserve seats.

------------------------------------------------------------------------

# 2. Primary Goal

The MVP focuses on validating the product idea.
Drivers to: Create routes, publish trips, manage passengers.
Passengers to: Browse trips, view details, reserve seats.
Prioritize simplicity, reliability, and fast interactions.

------------------------------------------------------------------------

# 3. Technology Stack

- **Language:** Kotlin
- **Architecture:** Clean Architecture + MVVM
- **UI Framework:** Compose Multiplatform
- **Platform:** Kotlin Multiplatform
- **Backend:** Firebase (Auth, Firestore, Storage)

------------------------------------------------------------------------

# 4. Architecture Principles

Follow Clean Architecture: **presentation → domain → data**.
- Presentation cannot depend on data.
- Domain cannot depend on data or frameworks (pure Kotlin).
- Data implements domain interfaces.
- Business logic exists only in the domain layer.

------------------------------------------------------------------------

# 5. Project Structure

Hybrid architecture (layers + features): `core/`, `data/`, `domain/`, `presentation/`.
Inside each layer, organize by feature (e.g., `presentation/auth/`).

------------------------------------------------------------------------

# 6. Core Module

Contains shared infrastructure: Result wrappers, dispatchers, shared extensions, base error abstractions, and **generic reusable UI components** (e.g., `AuthTextField`).

------------------------------------------------------------------------

# 7. Data Layer

Responsibilities: API communication, data sources, DTOs, mappers, and repository implementations.
Convert external errors into domain-safe errors.

------------------------------------------------------------------------

# 8. Domain Layer

Responsibilities: Domain models, business rules, use cases, and repository interfaces.
Pure Kotlin only. No dependency on frameworks or other layers.

------------------------------------------------------------------------

# 9. Presentation Layer & Modularization

Each screen must contain: `ViewModel`, `UiState`, `Action` sealed class, and the `Screen` composable.
**High Granularity Rule:** Screens must be composed of smaller, reusable private components (e.g., `LoginForm`, `RegisterForm`, `RoleSelectionSection`) defined within the same file or `components` package to improve readability and maintainability.

------------------------------------------------------------------------

# 10. ViewModel Rules

One ViewModel per screen. Use domain use cases, expose `StateFlow`, handle loading states, and map domain errors to UI state. ViewModels must not contain business logic.

------------------------------------------------------------------------

# 11. UI State

Immutable state classes (e.g., `LoginUiState`). Update state immutably and observe using `StateFlow`.

------------------------------------------------------------------------

# 12. Actions

Represent events from UI to ViewModel. UI calls `viewModel.onAction(Action)`.

------------------------------------------------------------------------

# 13. Naming Conventions

- **Interfaces:** `AuthRepository`
- **Implementations:** `AuthRepositoryImpl`
- **DTOs:** `UserDto`
- **Models:** `User`
- **Use Cases:** `LoginUseCase` (Verb + UseCase)

------------------------------------------------------------------------

# 14. Error Handling & UI Patterns

- Errors: Represented by sealed classes in the domain.
- **NO SNACKBARS:** Do not use `SnackbarHost` for validation or authentication errors.
- **Inline Errors:** Use `errorMessage` properties in text fields and the `ErrorMessage` component for global screen errors.
- Use `.asStringResource()` extensions to map domain errors to localized strings in the UI.

------------------------------------------------------------------------

# 15. Localization

No hardcoded strings. Use `Res.string.*` for all UI text.

------------------------------------------------------------------------

# 16. Iconography & Resources

- **NO `material-icons-extended`:** This library is forbidden due to size and performance.
- **Local XML Vectors:** Use only local XML vectors located in `composeResources/drawable`.
- **Access:** Use `vectorResource(Res.drawable.icon_name)`.
- **Material Symbols:** Prefer rounded or sharp Material Symbols exported as XML.

------------------------------------------------------------------------

# 17. Input Usability & IME

- **KeyboardOptions:** Disable `autoCorrect` for credentials (email, password). Use `KeyboardCapitalization.Words` for names.
- **IME Actions:** Use `ImeAction.Next` to move between fields and `ImeAction.Done` to trigger the primary action (Login/Register) from the last field.

------------------------------------------------------------------------

# 18. Navigation & Side-Effects

- Use `AppNavigation` with Type-Safe Routes (Kotlin Serialization).
- **ObserveAsEvents:** Use the `ObserveAsEvents` utility to handle one-time side-effects like navigation or showing success messages, triggered by a `SharedFlow` in the ViewModel.

------------------------------------------------------------------------

# 19. Security Guidelines

Validate inputs before sending requests. Never store passwords locally. Do not log sensitive info.

------------------------------------------------------------------------

# 20. Code Quality Standards

Follow SOLID, DRY, and Clean Architecture. Avoid business logic in UI, tight coupling, and hardcoded values.

------------------------------------------------------------------------

# 21. Expected AI Behavior

Strictly follow architecture, naming, and structure. Generated code must be production-ready and placed in the correct feature/layer. No pseudo-code.
