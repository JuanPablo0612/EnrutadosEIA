# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

EnrutadosEIA — a Kotlin Multiplatform / Compose Multiplatform carpooling app for university students. Drivers publish trips, passengers find and reserve seats. Currently targeting Android (iOS scaffold exists but is not active).

## Build & Run

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run tests
./gradlew :composeApp:testDebugUnitTest

# Clean build
./gradlew clean :composeApp:assembleDebug
```

Single module project (`composeApp`). Gradle configuration cache is enabled. JVM target is 17.

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

**Error handling** — sealed error classes in domain, mapped to localized strings via `.asStringResource()`. Inline errors in text fields + `ErrorMessage` component. **No SnackBars.**

**Navigation** — type-safe routes via `@Serializable sealed interface Route` + AndroidX Navigation Compose. One-time events use `ObserveAsEvents` utility with `SharedFlow`.

## DI (Koin)

Modules defined in `di/Modules.kt`: `authModule`, `routeModule`, `placeModule`, composed into `appModule`. Singletons for Firebase/repos, factories for use cases, `koinViewModel<T>()` for ViewModels.

## Key Conventions

- **Naming:** `AuthRepository` (interface), `AuthRepositoryImpl` (impl), `UserDto` (DTO), `User` (model), `LoginUseCase` (verb + UseCase)
- **Localization:** all UI strings via `Res.string.*` from `composeResources/values/strings.xml` (Spanish in `values-es/`). No hardcoded strings.
- **Icons:** local XML vectors in `composeResources/drawable/`. Access via `vectorResource(Res.drawable.icon_name)`. **`material-icons-extended` is forbidden.** If a new icon is needed, reference it in code and tell the user which icon to download.
- **Input UX:** disable `autoCorrect` for credentials, use `KeyboardCapitalization.Words` for names, `ImeAction.Next` between fields, `ImeAction.Done` on last field.
- **State:** immutable data classes, updated via `MutableStateFlow.update { }`.

## Tech Stack

- Kotlin 2.3.20, Compose Multiplatform 1.10.3, Material3
- Firebase Auth + Firestore + Analytics (Kotlin SDK `dev.gitlive:firebase-*` 2.4.0)
- Koin 4.2.0, AndroidX Navigation Compose 2.9.2, KotlinX Serialization
- FileKit 0.13.0, KotlinX DateTime 0.7.1
- Android: compileSdk 36, minSdk 24, targetSdk 36
