# Bookk Project Guidelines

Bookk is a Kotlin Multiplatform (KMP) mobile application for Android and iOS. It follows a clean architecture with a multi-module structure, emphasizing separation of concerns and platform-specific implementations where necessary.

## Architecture Overview

The project is organized into several top-level directories:

- `androidApp/`: Android-specific entry point and configuration.
- `iosApp/`: iOS-specific entry point and configuration (Swift).
- `shared/`: Orchestrates global DI and shared logic.
- `feature/`: Contains feature-specific modules (e.g., `authorization`, `dashboard`).
- `core/`: Core utilities, base classes, and shared business rules.
- `designsystem/`: Shared UI components, themes, and design tokens.
- `library/`: Independent, reusable modules (e.g., `cache`, `device`, `permissions`).

### Feature Module Structure

Each feature is typically split into the following sub-modules:

1. **`presentation`**:
   - `commonMain`: Contains `ViewModel`, `State` (interface), `NavigationDestination`, and `EventListener` (interface).
   - `androidMain`: Contains `Screen` (Compose UI), `AndroidState` (implementation using Compose `mutableStateOf`), and `NavigationGraph`.
   - `iosMain`: Platform-specific presentation logic for iOS.
2. **`domain`**:
   - `api`: Contains UseCase interfaces (usually functional interfaces with `operator fun invoke`) and domain entities.
   - `impl`: Contains UseCase implementations.
3. **`data`**:
   - `source`: (Optional) Shared data source interfaces.
   - `src/commonMain`: Contains Data Source implementations, Mappers, and Ktor-based networking logic.
   - `src/androidMain` / `src/iosMain`: Platform-specific data implementations (e.g., local storage, platform APIs).

## Coding Standards & Patterns

### 1. Presentation Layer (UDF)

- **State Interface**: Define UI state as an `interface` in `commonMain`. Use platform-specific implementations (e.g., `AndroidState` in `androidMain` and `IOSState` in Swift) to bridge with UI frameworks.
- **ViewModel**: Inherit from `me.bookk.core.presentation.ViewModel`. ViewModels implement the `EventListener` interface for the screen.
- **Event Listeners**: Define user actions in an interface (e.g., `SignInEventListener`) and implement it in the `ViewModel`.
- **Compose Screens (Android)**: Keep screens stateless by passing `State` and `EventListener`. Use `:designsystem` components whenever possible.
- **SwiftUI Screens (iOS)**: 
    - Implement `IOS...State` in Swift using `@Observable` and `NativeStateRepresentation`.
    - Use `@StateViewModel` to hold the ViewModel.
    - Use `.cast()` to get the native state implementation from the ViewModel's `uiState`.
    - Handle navigation and notifications using the provided view modifiers (e.g., `.handleNavigation`, `.handleNotifications`).
- **ViewModel Initialization (Up-to-date)**:
    - **Do NOT use `createInitData()` companion function** in `ViewModel`.
    - **Use the `setup()` extension function** pattern within the `ViewModel` to initialize the `uiState`.
    - Initialize `uiState` by calling `stateFactory.create${ScreenName}State().setup()`.
    - Use `weakSelfClosure` for listener callbacks within `setup()` to avoid memory leaks.
- **iOS DI Wiring**:
    - For every ViewModel, add a `@UsedInSwift` function in `Ios...PresentationDi.kt` to allow Swift to resolve it from Koin.
    - Use `KoinPlatform.getKoin().get()` to resolve the ViewModel in these helper functions.

### 2. Domain Layer (UseCases)

- **UseCases**: Use functional interfaces with a `suspend operator fun invoke()`.
- **Error Handling**: Define errors as sealed classes within the UseCase interface.
- **Logic**: Keep business logic in the `domain` layer, away from UI and data implementation details.

### 3. Data Layer

- **Networking**: Use Ktor with the `Resources` plugin for type-safe routing.
- **Mapping**: Always map remote/local models to domain entities.
- **Error Mapping**: Use `runCatching` and `mapExceptions` (from `core.data.DataSource`) to map platform/network errors to domain-specific errors.
- **Persistence**: Use the `library/cache` module for simple key-value storage or Room for complex data.

### 4. Dependency Injection (Koin)

- Define DI modules in each layer (e.g., `AuthPresentationDi.kt`, `AuthDomainDi.kt`, `AuthDataDi.kt`).
- Wire them up in `shared/src/commonMain/kotlin/me/bookk/di/DISetup.kt`.

### 5. Resources

- Use **moko-resources** for strings, images, and colors to ensure cross-platform availability.
- Access strings via `AuthRes.strings.my_string_key.desc()`.

## Feature Creation

To create a new feature, use the `feature/.template/feature.template.sh` script:
```bash
cd feature/.template
./feature.template.sh <feature_name> <FeatureNameCapitalized>
```
This script scaffolds the module structure and performs initial DI wiring.

## Screen Creation

To create a new screen within an existing feature, use the `feature/.template/screen.template.sh` script:
```bash
cd feature/.template
./screen.template.sh <feature_name> <ScreenName> <screen.package>
```
Example:
```bash
./screen.template.sh services Details service.details
```

## Commands

- **Build Android**: `./gradlew :androidApp:assembleDevDebug`
- **Run Tests**: `./gradlew test` (runs common and Android tests)
- **Lint**: `./gradlew detekt` (if configured)

## AI Agent Interaction Rules

- **Research First**: Always check for existing patterns in similar features before implementing new ones.
- **Respect Boundaries**: Keep implementation details (e.g., Ktor models, SQL queries) within the `data` layer and never leak them to `presentation`.
- **UDF Integrity**: Do not bypass the `State` interface pattern.
- **Mocking**: For "mock" build variants, provide mock implementations of data sources or use Ktor's `MockEngine`.
