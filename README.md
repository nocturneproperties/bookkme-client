# Bookk

**Bookk** is a Kotlin Multiplatform (KMP) mobile app for **Android and iOS** that helps businesses manage bookings — appointments, clients, services, and business settings from a single shared codebase.

The business logic, view models, and data layer are written once in Kotlin and shared across both platforms. Only the UI is native: **Jetpack Compose** on Android and **SwiftUI** on iOS.

---

## Highlights

-  **Kotlin Multiplatform** — domain, data, and presentation (ViewModels + UI state) live in `commonMain`.
-  **Native UI on both platforms** — Compose (`androidMain`) and SwiftUI (`iosApp/`) render the same shared state.
-  **Clean, modular architecture** — every feature is split into `presentation` / `domain` (`api` + `impl`) / `data` (`source` + implementation) modules with strict layer boundaries.
-  **Unidirectional data flow** — the UI only reads `State` interfaces; user actions and navigation flow back through state callbacks.
-  **Logic-first testing** — every use case is unit-tested in `commonTest` with the Mokkery mocking framework and a given/when/then structure.
-  **Reusable design system** — shared `designsystem` module maps directly to screen building blocks (fields, pickers, lists, dialogs, buttons).

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Kotlin `2.3.21` (Multiplatform) |
| Android UI | Jetpack Compose (Material 3) |
| iOS UI | SwiftUI |
| Dependency Injection | [Koin](https://insert-koin.io/) `4.2.2` |
| Networking | [Ktor](https://ktor.io/) `3.5.1` (typed `Resources` routes, `application/x-protobuf`) |
| Local persistence | Room `2.8.4` + DataStore preferences |
| Async | Kotlin Coroutines `1.11.0` |
| Resources / i18n | [moko-resources](https://github.com/icerockdev/moko-resources) |
| Images | Coil `3.5.0` |
| Push / crash / analytics | Firebase (`34.15.0`) |
| Testing / mocking | kotlin-test + [Mokkery](https://mokkery.dev/) `3.4.2` |

**Build tooling:** Android Gradle Plugin `9.2.1`, KSP, custom Gradle convention plugins (`build-src`), version catalog (`gradle/libs.versions.toml`).

**Platform targets:** Android `minSdk 28` / `compileSdk 37`, iOS via the `shared` KMP framework. App package: `me.bookk.android`.

---

## Repository Layout

```
Bookk/
├── androidApp/            Android entry point & app configuration
├── iosApp/                iOS app (SwiftUI, Xcode file-system-synchronized groups)
├── shared/                Global DI wiring + cross-feature StateFactoryCreator
├── database/              Room database, entities, DAOs, migrations
├── core/                  Base classes: ViewModel, DataSource, navigation primitives
│   ├── data/  ├── domain/  ├── presentation/  └── testFixtures/
├── designsystem/          Shared UI components + reusable UI-state primitives
├── environment/           Environment config (api + impl)
├── library/               Independent reusable modules
│   ├── cache/  credentials/  permissions/  files/  device/
│   ├── money/  biometry/  validation/  picker/  notifications/
├── feature/               Feature modules (see below)
└── build-src/             Gradle convention plugins & app constants
```

### Feature modules

Each feature is a set of KMP modules following the same shape:

```
feature/<name>/
├── presentation/   ViewModels, State interfaces, Destinations, StateFactory,
│                   Compose screens (androidMain), SwiftUI accessors (iosMain)
├── domain/
│   ├── api/        UseCase interfaces + domain entities
│   └── impl/       UseCase implementations + unit tests
└── data/
    ├── source/     DataSource interfaces
    └── (impl)      Ktor routing/models, mappers, DataSource impls, DI
```

Features: **authorization**, **business**, **appointments**, **clients**, **services**, **dashboard**, **settings**.

> **Canonical reference screen:** `BusinessPlugins` (`feature/business`, `screen/plugins`) demonstrates the current architecture pattern. See [`AGENTS.md`](AGENTS.md) for the full screen-creation, use-case, and testing conventions.

---

## Architecture

Bookk uses a layered, unidirectional-data-flow architecture:

- **Presentation** — ViewModels expose a `State` interface built by a per-feature `StateFactory`. State is created parameterless and configured in a `setup()` extension; all callbacks use `weakSelfClosure` to avoid iOS retain cycles. UI reads state only.
- **Domain** — Use cases are verb-named interfaces (`api`) with `internal` implementations (`impl`). Domain errors are exception classes nested in a `sealed interface Error`.
- **Data** — Ktor models, SQL, and platform APIs never leak past the `data` boundary. Remote/local models are always mapped to domain entities. Each DataSource method does exactly one thing (fetch **or** read DB **or** write DB); use cases orchestrate multi-step operations.
- **DI** — Koin wires presentation (`viewModelOf` / `factoryOf`), domain, and data modules, aggregated per feature under `shared/di/feature/`.

Build flavors (`dimension = "type"`): **`dev`**, **`stage`**, **`prod`** (defined in `build-src`).

---

## Getting Started

### Prerequisites

- **JDK 17+**
- **Android Studio** (latest stable) with the Android SDK
- **Xcode** (for iOS) on macOS
- A running local backend instance for API access (see [API docs](#api-documentation))

### Clone

```bash
git clone git@github.com:an22/bookk.me-mobile.git
cd bookk.me-mobile
```

Create a `local.properties` with your Android SDK path (Android Studio generates this on first open):

```properties
sdk.dir=/Users/you/Library/Android/sdk
```

### Run Android

Open the project in Android Studio and run the `androidApp` configuration, or from the command line:

```bash
./gradlew :androidApp:assembleDevDebug
```

### Run iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run the app. The shared Kotlin framework is built automatically. To quickly compile the shared code for iOS from the CLI:

```bash
./gradlew :shared:compileKotlinIosArm64
```

---

## Development

### Common Gradle commands

| Task | Command |
|---|---|
| Build Android (dev debug) | `./gradlew :androidApp:assembleDevDebug` |
| Compile shared for iOS | `./gradlew :shared:compileKotlinIosArm64` |
| Run tests (common + Android) | `./gradlew test` |
| Lint | `./gradlew detekt` |

### Adding a new screen

Scaffold with the template script, then complete the wiring checklist in [`AGENTS.md`](AGENTS.md):

```bash
cd feature/.template
./screen.template.sh <feature_name> <ScreenName> <screen.package>
# example: ./screen.template.sh business Plugins screen.plugins
```

A screen is **not done until both platforms are wired**: commonMain state/VM/destinations, Android state/screen/nav/DI/factory, and iOS Kotlin DI accessors + SwiftUI state/screen/factory/navigation.

### Testing

Testing is **logic-only** (no UI/instrumentation/snapshot tests). Every `<Name>Impl` use case gets a `<Name>ImplTest.kt` in `domain/impl/src/commonTest`, written test-first (given/when/then) with Mokkery and the `Fixture` pattern. See the **Testing Rules** section of [`AGENTS.md`](AGENTS.md) for the required style.

---

## API Documentation

REST API specs are served by the local backend instance:

```
http://localhost/api/{feature_name}/internal/swagger/documentation.yaml
```

`{feature_name}` accepts short or long forms (e.g. `auth`/`authorization`, `appointments`, `clients`, `services`, `business`).

> ⚠️ API requests/responses are serialized as `application/x-protobuf` with **positional** field numbers. When adding, removing, or reordering a field on any `*Remote`/`*Request` model, verify the Kotlin property order matches the backend schema exactly — a mismatch silently desyncs the wire format at runtime. Always fetch the current swagger schema before editing a contract.

---

## Contributing

Please read [`AGENTS.md`](AGENTS.md) before contributing — it is the authoritative guide to the project's architecture, screen/use-case creation patterns, DI wiring, design-system components, and testing rules. Key expectations:

- Respect layer boundaries (Ktor/SQL/platform APIs stay in `data`).
- No hardcoded user-visible strings — use moko-resources `strings.xml`.
- Follow the current screen pattern (parameterless state + `setup()`); never copy deprecated `InitData` screens.
- Unit-test every use case implementation.

---

## License

Licensed under the [Apache License 2.0](LICENSE).

```
Copyright 2026 Mykhailo Antiufieiev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
