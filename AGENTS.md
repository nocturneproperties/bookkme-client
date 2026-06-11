# Bookk Project Guidelines

Bookk is a Kotlin Multiplatform (KMP) mobile application for Android and iOS. Android UI is Jetpack Compose (`androidMain` of presentation modules); iOS UI is SwiftUI (`iosApp/`). ViewModels, state interfaces, domain and data layers are shared in `commonMain`.

> **Canonical reference screen**: `BusinessPlugins` (feature/business, `screen/plugins` package + `iosApp/iosApp/Feature/Business/Plugins/`). It is the newest screen and demonstrates the current pattern.
> **Outdated pattern — do NOT copy**: screens whose state is created with a `State.InitData` parameter (e.g. business `bootstrap`, `create`, `settings`). New screens must use parameterless state creation + the `setup()` extension pattern described below.

## Architecture Overview

Top-level directories:

- `androidApp/`: Android entry point and configuration.
- `iosApp/`: iOS app (SwiftUI). Uses Xcode file-system-synchronized groups, so new `.swift` files under `iosApp/iosApp/` are picked up automatically — no pbxproj editing needed.
- `shared/`: Global DI wiring (`me/bookk/di/DISetup.kt`, `me/bookk/di/feature/*DI.kt`) and the cross-feature `StateFactoryCreator`.
- `feature/`: Feature modules (`authorization`, `business`, `appointments`, `clients`, `dashboard`, `services`, `settings`, …).
- `core/`: Base classes (`core/presentation/ViewModel`, `core/data/DataSource`, navigation primitives).
- `designsystem/`: Shared UI components and reusable UI-state primitives (`AppBarState`, `ButtonState`, `ListState`, `NavigationState`, `PresentationNotificationState`, plus their `Android*`/`IOS*` implementations).
- `library/`: Independent reusable modules (`cache`, `device`, `permissions`, `picker`, `credentials`, …).

### Feature Module Structure

```
feature/<name>/
  presentation/
    src/commonMain/  ViewModels, State interfaces, Destinations, <Feature>StateFactory,
                     di/<Feature>Di.kt (expect platform module), moko-resources strings
                     Screens live under .../presentation/screen/<screenname>/
    src/androidMain/ Compose Screens, Android*State impls, NavGraph destinations,
                     factory/Android<Feature>StateFactory.kt, di/<Feature>Di.android.kt
    src/iosMain/     di/IOS<Feature>Di.kt (actual module + @UsedInSwift VM accessors)
  domain/
    api/             UseCase interfaces + domain entities (commonMain)
    impl/            UseCase implementations + di/DI.kt (commonMain, internal classes)
  data/
    source/          Data source INTERFACES, package me.bookk.feature.<name>.domain.datasource
    src/commonMain/  Common*DataSource impls, Ktor routing/models, mappers, di/<Feature>DataDi.kt
```

iOS UI for a feature lives in `iosApp/iosApp/Feature/<FeatureCapitalized>/`, one folder per screen, plus `IOS<Feature>StateFactory.swift`.

## Screen Creation (the current pattern)

Scaffold with the template script (it generates code in the up-to-date pattern):

```bash
cd feature/.template
./screen.template.sh <feature_name> <ScreenName> <screen.package>
# example: ./screen.template.sh business Plugins screen.plugins
```

⚠️ The script's DI/factory wiring steps only run if it finds files at its expected paths, and some features use different file names (e.g. it looks for `Ios<Feature>PresentationDi.kt` but business uses `IOS<Feature>Di.kt`). **Always verify every item in the checklist below after running it**, and wire manually whatever the script skipped.

### Checklist — files & wiring for a new screen `Foo` in feature `bar`

**commonMain** (`feature/bar/presentation/src/commonMain/.../presentation/screen/foo/`):

1. `FooState.kt` — `interface FooState` with `val appBar: AppBarState`, screen fields (`var x: StringDesc`, `var isY: Boolean`, sub-state interfaces, `ListState<...>`, `ButtonState`), `val notifications: PresentationNotificationState`, `val navigation: NavigationState<FooDestinations>`.
2. `FooDestinations.kt` — `sealed class FooDestinations : NavigationDestination()` with `data object Back` and any forward destinations.
3. `FooViewModel.kt`:
   ```kotlin
   class FooViewModel(
       @InjectedParam private val businessId: Uuid,   // screen args, if any
       private val someUseCase: SomeUseCase,
       stateFactory: BarStateFactory,
       vmArgs: VmArgs
   ) : ViewModel(vmArgs) {
       val uiState: FooState = stateFactory.createFooState().setup()

       private fun FooState.setup() = apply {
           appBar.title = BarRes.strings.foo_title.desc()
           appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(FooDestinations.Back) }
           button.onClick = weakSelfClosure { it.doSomething() }
       }
   }
   ```
   - **No `InitData`, no `createInitData()`** — state factory methods take no parameters; everything is set in `setup()`.
   - All callbacks inside `setup()` use `weakSelfClosure { it... }` (from `core.presentation.memory`) to avoid retain cycles on iOS.
   - Async work uses the base-class `launch` helper:
     ```kotlin
     launch(
         launchIn = DispatcherProvider.io,
         onStart = { uiState.button.startLoading() },
         call = { someUseCase(businessId) },
         onComplete = { uiState.x = it },
         onError = { uiState.notifications.add(it.notification()) },
         onTerminate = { uiState.button.stopLoading() }
     )
     ```
     Handle known use-case errors with a `when (it)` in `onError`; fall back to `uiState.notifications.add(it.notification())`.
4. Add `fun createFooState(): FooState` to `BarStateFactory` (and one method per sub-state interface if the screen has reusable sub-states, e.g. `createBusinessPluginState()`).

**androidMain**:

5. `AndroidFooState.kt` — `internal class AndroidFooState : FooState`, scalar fields via `by mutableStateOf(...)`, composites via design-system impls (`AndroidAppBarState()`, `AndroidButtonState()`, `AndroidListState()`, `AndroidNavigationState()`, `AndroidNotificationState()`).
6. `FooScreen.kt` — stateless `@Composable internal fun FooScreen(state: FooState)`; `Scaffold` + `AppTopBar(state.appBar)`; use `:designsystem` components; render moko strings with `.localized()`.
7. `FooDestination.kt` — nav-graph entry:
   ```kotlin
   internal fun NavGraphBuilder.fooScreen(navigation: BarNavigation) {
       composable<BarDestination.Foo>(typeMap = mapOf(serializableNavTypeEntry<Uuid>())) {
           val entry = it.toRoute<BarDestination.Foo>()
           val viewModel: FooViewModel = koinViewModel { parametersOf(entry.id) }
           CompositionLocalProvider(LocalNavigation provides navigation) {
               ObserveNotifications(viewModel.uiState.notifications)
               SendLifecycleEventsTo(viewModel)
               FooScreen(viewModel.uiState)
           }
           ObserveNavigation(viewModel.uiState.navigation) {
               when (it) {
                   FooDestinations.Back -> { /* onBackPressedDispatcher */ }
               }
           }
       }
   }
   ```
8. Add `@Serializable data class Foo(val id: Uuid)` (or `data object`) to `navigation/BarDestinations.kt` (commonMain), register `fooScreen(navigation)` in `navigation/BarGraph.kt`, and add a `toFoo` lambda to the feature's `BarNavigation`/`LocalNavigation` if other screens navigate to it.
9. Add `override fun createFooState() = AndroidFooState()` to `factory/AndroidBarStateFactory.kt`.
10. Register the ViewModel in `di/BarDi.android.kt`: `viewModelOf(::FooViewModel)`.

**iosMain**:

11. In `di/IOSBarDi.kt`: add `factoryOf(::FooViewModel)` to `platformBarDiModule()` and a Swift accessor:
    ```kotlin
    @UsedInSwift
    fun fooVM(id: Uuid): FooViewModel =
        KoinPlatform.getKoin().get(parameters = { parametersOf(id) })
    ```

**iosApp** (`iosApp/iosApp/Feature/Bar/Foo/`):

12. `IOSFooState.swift`:
    ```swift
    @Observable
    @MainActor
    class IOSFooState: @MainActor FooState, NativeStateRepresentation {
        typealias SwiftType = IOSFooState
        typealias KotlinType = FooState
        let appBar: any AppBarState
        var title: any StringDesc          // default: RawStringDesc(string: "")
        let navigation: any NavigationState
        let notifications: any PresentationNotificationState
        init() { appBar = IOSAppBarState(); /* ... IOSListState<...>(), IOSButtonState() ... */ }
    }
    ```
    Sub-state classes also conform to `NativeStateRepresentation` so views can `IOSFooSubState.cast(kotlinState)`.
13. `FooScreen.swift`:
    ```swift
    struct FooScreen: View {
        @EnvironmentObject var navigationStack: NavigationStackHolder
        @StateViewModel var viewModel: FooViewModel

        init(businessId: KotlinUuid) {
            self._viewModel = StateViewModel(wrappedValue: IOSBarDiKt.fooVM(id: businessId))
        }

        var body: some View {
            ScrollView { /* content */ }
                .withNavigationBar(viewModel.uiState.appBar)
                .sendLifecycleEventsTo(viewModel)
                .handleNotifications(viewModel.uiState.notifications)
                .handleNavigation(viewModel.uiState.navigation) { dest in
                    switch dest {
                    case is FooDestinations.Back: navigationStack.popLast()
                    default: break
                    }
                }
        }
    }
    ```
14. Add `func createFooState() -> any FooState { IOSFooState() }` to `IOS<Bar>StateFactory.swift`.
15. Wire forward navigation: register `.navigationDestination(for: ...)` in the feature tab (e.g. `BusinessTab.swift`) mapping the destination class pushed by the source screen's ViewModel to `FooScreen(...)`. Source screens push via `uiState.navigation.push(...)`; SwiftUI `NavigationLink(value:)` over `NavigationStack(path: $navigationStack.path)` also works for list items.

**Strings**: add keys to `feature/bar/presentation/src/commonMain/moko-resources/base/strings.xml`. Access in Kotlin: `BarRes.strings.key.desc()`; in Compose render with `.localized()`; in Swift: `BarRes.strings().key.desc().localized()`.

## Use Case (operation) Creation

1. **Interface** in `feature/<name>/domain/api` (one file per use case, verb-first name):
   ```kotlin
   interface EnableAppointmentsPlugin {
       suspend operator fun invoke(businessId: Uuid)

       sealed interface Error {
           class AlreadyEnabled : Exception()
       }
   }
   ```
   Domain errors are exception classes nested in a `sealed interface Error` inside the use case interface.
2. **Implementation** in `feature/<name>/domain/impl`, `internal class <Name>Impl(dataSources...) : <Name>`. Depend on data-source **interfaces** from the `data/source` module (package `me.bookk.feature.<name>.domain.datasource`). Map data errors to domain errors (`runCatching { ... }.onBusinessError { when (it.errorCode) { ... -> throw Error.X() } }`).
3. **Register** in `domain/impl/.../di/DI.kt`: `factoryOf(::EnableAppointmentsPluginImpl) bind EnableAppointmentsPlugin::class`.
4. **Inject** into the ViewModel constructor (Koin resolves it on both platforms — no extra per-platform wiring for use cases).

If a new data-source method is needed: add it to the interface in `data/source`, implement in `Common<X>DataSource(httpClient) : DataSource(), <X>DataSource` in `data/src/commonMain` wrapping calls in `mapExceptions { ... }`, using Ktor `Resources` typed routes and request/response models in `data/remote/`. Register with `singleOf(::Common<X>DataSource) bind <X>DataSource::class` in the feature's data DI module. Always map remote/local models to domain entities — never leak Ktor models out of `data`.

## Dependency Injection (Koin)

- Presentation: `di/<Feature>Di.kt` (commonMain) declares `internal expect fun platform<Feature>DiModule(): Module` and a public `<feature>PresentationModule()`; actuals in `androidMain` (`viewModelOf`) and `iosMain` (`factoryOf` + `@UsedInSwift` accessors). ViewModel screen arguments are passed with `@InjectedParam` + `parametersOf(...)` at resolution.
- Domain: `<feature>DomainModule()` in `domain/impl`. Data: `<feature>DataModule()` in `data`.
- Per-feature aggregation: `shared/src/commonMain/kotlin/me/bookk/di/feature/<Feature>DI.kt` includes presentation + data + domain modules; installed in `shared/.../di/DISetup.kt`.
- New feature module set: add to `settings.gradle.kts`, `shared/build.gradle.kts` deps, create the `<Feature>DI.kt` aggregator, add the feature's `StateFactory` to `shared/.../presentation/StateFactoryCreator.kt`, and implement it in both `iosApp/iosApp/Core/IOSStateFactoryCreator.swift` and the Android creator. Prefer the scaffold script: `cd feature/.template && ./feature.template.sh <feature_name> <FeatureNameCapitalized>`.

## Commands

- **Build Android**: `./gradlew :androidApp:assembleDevDebug`
- **Compile shared for iOS (quick check of commonMain/iosMain code)**: `./gradlew :shared:compileKotlinIosArm64`
- **Run Tests**: `./gradlew test` (runs common and Android tests)
- **Lint**: `./gradlew detekt` (if configured)

## AI Agent Interaction Rules

- **Use the newest screen as reference** (currently `BusinessPlugins`). Never copy from screens that pass `InitData` into state factories — that pattern is deprecated.
- **A "new screen" task is not done until both platforms are wired**: commonMain state/VM/destinations, Android state/screen/nav/DI/factory, iOS Kotlin DI accessor, Swift state/screen, Swift factory method, and navigation registration on both platforms.
- **Respect layer boundaries**: Ktor models, SQL, platform APIs stay in `data`; `presentation` only sees use cases and domain entities; ViewModels never touch data sources directly.
- **UDF integrity**: UI reads only `State` interfaces; user actions flow through state callbacks set in `setup()`; navigation flows through `NavigationState.push(...)` and is observed at the edge (`ObserveNavigation` / `.handleNavigation`).
- **Persistence**: use `library/cache` for simple key-value storage or Room for complex data.
- **Mocking**: for "mock" build variants provide `RoutingMock` implementations or Ktor `MockEngine`.
