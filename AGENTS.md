# AGENTS.md

This document defines the rules, architecture, conventions, and
expectations that any AI agent must follow when working on this
repository. The goal is to ensure that all generated code is consistent,
maintainable, scalable, and aligned with the project's architecture.

Agents must strictly follow these guidelines when generating, modifying,
or refactoring code.

------------------------------------------------------------------------

# 1. Project Overview

CampusRide is a mobile application designed to organize the informal
carpooling system used by university students.

Currently, rides are typically coordinated through chat groups such as
WhatsApp, which leads to:

-   Disorganized communication
-   Difficulty tracking available seats
-   Unclear routes
-   Message overload
-   Lack of structured trip information

CampusRide replaces this system with a structured platform where drivers
can publish trips and passengers can easily find and reserve seats.

The application focuses on university communities.

------------------------------------------------------------------------

# 2. Primary Goal

The MVP focuses on validating the product idea rather than monetization.

The application must allow:

Drivers to: - Create routes - Publish trips - Offer available seats -
Manage passengers

Passengers to: - Browse available trips - View route details - Reserve
seats

The MVP prioritizes:

-   Simplicity
-   Reliability
-   Clear information
-   Fast interactions

Complex systems such as payments, analytics, or recommendation engines
are out of scope for the MVP.

------------------------------------------------------------------------

# 3. Technology Stack

Language: Kotlin

Architecture: Clean Architecture + MVVM

UI Framework: Compose Multiplatform

Platform: Kotlin Multiplatform

Backend: Firebase

Firebase services used:

-   Firebase Authentication
-   Firestore
-   Firebase Storage
-   Firebase Cloud Messaging (future)

The architecture must allow replacing Firebase in the future.

------------------------------------------------------------------------

# 4. Architecture Principles

The project follows Clean Architecture.

Dependency flow:

presentation → domain → data

Rules:

-   Presentation cannot depend on data
-   Domain cannot depend on data
-   Data implements domain interfaces
-   Domain must remain framework-independent

Business logic must only exist in the domain layer.

------------------------------------------------------------------------

# 5. Project Structure

The repository uses a hybrid architecture combining layers and features.

Top-level structure:

shared/ core/ data/ domain/ presentation/

Inside each layer, code is organized by feature.

Example:

data/auth/ domain/auth/ presentation/auth/

Agents must not mix features across directories.

------------------------------------------------------------------------

# 6. Core Module

The core module contains shared infrastructure and utilities.

Examples:

-   Result wrappers
-   Coroutine dispatchers
-   Shared extensions
-   Base error abstractions
-   Shared UI components

Feature-specific logic must not be placed inside core.

------------------------------------------------------------------------

# 7. Data Layer

Structure example:

data/auth/

remote/ local/ model/ mapper/ repository/

Responsibilities:

-   External API communication
-   Remote data sources
-   Local persistence
-   DTO models
-   Mapping between DTO and domain models
-   Repository implementations

Rules:

-   No business logic
-   No UI logic
-   Convert external errors into domain-safe errors

------------------------------------------------------------------------

# 8. Domain Layer

Structure example:

domain/auth/

model/ repository/ use_case/

Responsibilities:

-   Domain models
-   Business rules
-   Use cases
-   Repository interfaces

Rules:

-   Must be pure Kotlin
-   Must not depend on frameworks
-   Must not depend on Firebase
-   Must not depend on the data layer

------------------------------------------------------------------------

# 9. Presentation Layer

Structure example:

presentation/auth/

login/ register/ common/

Each screen must contain:

-   ViewModel
-   UiState
-   Action sealed class
-   Screen composable

Example:

presentation/auth/login/

LoginViewModel LoginUiState LoginAction LoginScreen

------------------------------------------------------------------------

# 10. ViewModel Rules

Each screen must have its own ViewModel.

For authentication:

LoginViewModel RegisterViewModel

Do NOT create a single AuthViewModel.

ViewModels must:

-   Use domain use cases
-   Expose StateFlow
-   Handle loading states
-   Handle domain errors
-   Map domain models to UI state

ViewModels must not contain business logic.

------------------------------------------------------------------------

# 11. UI State

Each screen must define an immutable UI state class.

Example:

LoginUiState

Typical fields:

-   email
-   password
-   isLoading
-   error

State must be updated immutably.

UI must observe state using StateFlow.

------------------------------------------------------------------------

# 12. Actions

Actions represent events sent from the UI to the ViewModel.

Direction:

UI → ViewModel

Examples:

LoginAction

OnEmailChanged OnPasswordChanged OnLoginClicked OnClearError

Important rule:

The ViewModel does not send actions to any other layer.

The ViewModel receives actions and updates the UI state.

------------------------------------------------------------------------

# 13. Naming Conventions

Interfaces:

AuthRepository TripRepository RouteRepository

Implementations:

AuthRepositoryImpl TripRepositoryImpl

DTOs:

UserDto TripDto RouteDto

Domain models:

User Trip Route Booking

Use cases:

LoginUseCase RegisterUseCase CreateTripUseCase BookSeatUseCase

Use cases must follow the pattern:

Verb + UseCase

------------------------------------------------------------------------

# 14. Error Handling

Errors must be represented using sealed classes in the domain layer.

Example:

AuthError TripError BookingError

External errors must be mapped to domain errors inside the data layer.

UI must only interact with domain errors.

Never expose Firebase exceptions to the presentation layer.

------------------------------------------------------------------------

# 15. Localization

Strings must not be hardcoded.

All UI strings must use a centralized localization system.

Example keys:

login_title email_placeholder password_placeholder login_button
register_button error_invalid_credentials error_network

This allows future multi-language support.

------------------------------------------------------------------------

# 16. Security Guidelines

Agents must ensure:

-   Passwords are never stored locally
-   Inputs are validated before sending requests
-   Sensitive information is not logged
-   Backend error messages are not exposed directly to the UI

------------------------------------------------------------------------

# 17. MVP Features

The MVP includes:

Authentication Routes Trips Bookings

Future features may include:

Ratings Notifications Messaging Payments

Architecture must remain flexible for expansion.

------------------------------------------------------------------------

# 18. Authentication Scope

Authentication currently supports:

-   Email registration
-   Email login
-   Logout
-   Authentication state observation

Authentication is implemented using Firebase Authentication.

However, the architecture must allow replacing Firebase in the future.

------------------------------------------------------------------------

# 19. Code Quality Standards

Agents must follow:

-   SOLID principles
-   Clear naming conventions
-   Immutable state
-   Clean architecture boundaries
-   Dependency inversion

Agents must avoid:

-   Business logic in UI
-   Tight coupling
-   Code duplication
-   Circular dependencies
-   Hardcoded values

------------------------------------------------------------------------

# 20. Expected AI Behavior

When generating code, AI agents must:

-   Follow the project architecture strictly
-   Place files in the correct feature and layer
-   Respect naming conventions
-   Avoid unnecessary abstractions
-   Prefer readability and maintainability

All generated code must be production-ready Kotlin code.

Pseudo-code is not allowed.
