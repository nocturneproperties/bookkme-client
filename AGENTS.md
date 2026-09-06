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
3. **Test by default**: every `<Name>Impl` gets a `<Name>ImplTest.kt` in `domain/impl/src/commonTest` — this is not optional or something to ask about, it is part of creating the use case. See `## Testing Rules` below for the required style (given/when/then, Mokkery, Fixture pattern); cover the happy path, the cached/DB-first branch if applicable, and every `Error` variant.
4. **Register** in `domain/impl/.../di/DI.kt`: `factoryOf(::EnableAppointmentsPluginImpl) bind EnableAppointmentsPlugin::class`.
5. **Inject** into the ViewModel constructor (Koin resolves it on both platforms — no extra per-platform wiring for use cases).

If a new data-source method is needed: add it to the interface in `data/source`, implement in `Common<X>DataSource(httpClient) : DataSource(), <X>DataSource` in `data/src/commonMain` wrapping calls in `mapExceptions { ... }`, using Ktor `Resources` typed routes and request/response models in `data/remote/`. Register with `singleOf(::Common<X>DataSource) bind <X>DataSource::class` in the feature's data DI module. Always map remote/local models to domain entities — never leak Ktor models out of `data`.

**Remote model wire numbers are pinned explicitly.** API requests/responses are serialized as `application/x-protobuf` via `kotlinx-serialization-protobuf` (`core/data/.../Serialization.kt`). Every property on a `*Remote`/`*Request` model carries an explicit `@ProtoNumber(n)` (`kotlinx.serialization.protobuf.ProtoNumber`), numbered 1..N in declaration order. When adding a field, append it at the end of the constructor with the next unused number — never renumber or reuse an existing field's number, even if properties are reordered for readability. When removing a field, do not recycle its number for a new field. Fetch the current schema from the swagger endpoint (see API Documentation section) to confirm the field exists and get its type before wiring it up; a `*RemoteContractTest` alongside the model pins the `(fieldName, protoNumber)` pairs — update it in the same change. A wrong or reused number silently desyncs the wire format — Kotlin won't catch it, only a runtime bug will.

### DataSource method contract

Each datasource method must do **one thing**: either fetch from network, read from DB, or write to DB — never combine these in a single method. When a use case needs to fetch and then cache, it calls two separate datasource methods (e.g. `getAppointmentsForDate` then `saveAppointmentsForDate`). The use case is the only orchestrator of multi-step data operations.

### Mapper placement

- **Remote → domain**: embed as an instance method directly inside the remote model class (e.g. `fun toDomain()` in `AppointmentRemote`).
- **Domain ↔ local (DB entity)**: the `database` module must not depend on feature domain-api modules. Because of this cross-module boundary, embed mappers inside entities is not possible. Use a dedicated `<Feature>Mapper.kt` file in `feature/<name>/data/src/commonMain/…/mapping/` that contains extension functions (e.g. `Appointment.toEntity()`, `AppointmentLocal.toDomain()`). Never add domain-api dependencies to the `database` module to work around this.

### Local (Room) caching pattern

When adding local caching for a new entity:

1. **`database` module**: create `<X>Entity` (flat columns, `@PrimaryKey`), a child entity for any collection fields (composite PK + FK + CASCADE delete), and a relation class `<X>Local` (`@Embedded` + `@Relation`). Add an `<X>Dao` with upsert, delete, and a `@Transaction open suspend fun upsertWithChildren(...)` that deletes stale children before re-inserting. Register the entities in `AppDatabase`, bump `version` by 1, and add `AutoMigration(from = N, to = N+1)` — Room generates the migration automatically for additive schema changes (new tables only).
2. **`data/source`**: add a `saveFor*(...)` method to the datasource interface.
3. **`data/src/commonMain`**: implement the save method in `Common<X>DataSource` (DB write only). Embed domain→entity conversion as private extensions inside the datasource file. Keep the network-fetch method a pure network call.
4. **`domain/impl`**: call the save method from the use case after a successful fetch, using `.also { dataSource.saveFor*(...) }`.

### Cache-then-remote (`cached()`) pattern

When a use case exposes both `invoke()` and a `cached(businessId, onResultAvailable: suspend (T) -> Unit)` — emit the DB value first (if there is one worth showing), then the freshly-fetched remote value (see `GetClientsListImpl`, `GetEmployeesImpl`, `GetEmployeeInvitationsImpl`, `GetServicesImpl`, `GetServiceGroupsImpl` for the canonical shape):

```kotlin
override suspend fun invoke(businessId: Uuid): List<X> {
    return dataSource.getX(businessId).also {
        dataSource.deleteXInDb()
        dataSource.saveXInDb(it)
        dataSource.saveLastSyncedAt(businessId)
    }
}

override suspend fun cached(businessId: Uuid, onResultAvailable: suspend (List<X>) -> Unit) {
    if (dataSource.getLastSyncedAt(businessId) != null) {
        onResultAvailable(dataSource.getXFromDb(businessId))
    }
    onResultAvailable(invoke(businessId))
}
```

**Never gate the cached emission on `it.isNotEmpty()` (or, for a single value, on a falsy/default placeholder).** An empty list or a default `false`/`null`-coalesced value is indistinguishable from "never synced yet" — skipping the callback in both cases means a business that has genuinely zero items (or a plugin that's genuinely disabled) sits on a loading spinner until the redundant remote call returns, instead of showing the correct state immediately from cache. Track sync state explicitly instead:

- **List/collection results**: add `getLastSyncedAt(businessId): Instant?` and `saveLastSyncedAt(businessId)` to the datasource interface, backed by a `PreferenceProvider` bucket (e.g. `preferenceProvider.get("<feature>_prefs")`, key `"last_synced_at_$businessId"`, value `Clock.System.now().toEpochMilliseconds()`) — not a new Room table. `cached()` checks `getLastSyncedAt(businessId) != null`, not the list's emptiness.
- **Single nullable domain record** (e.g. `GetNotificationSettingsImpl`, `GetAppointmentSettingsImpl`): `null` from the DB is already unambiguous — there's no "confirmed absent" state for a 1:1 settings row — so `dataSource.getXFromDb()?.let { onResultAvailable(it) }` is correct as-is and needs no marker.
- **Single Boolean/enum-like result** (e.g. `IsAppointmentsPluginEnabledImpl`): make the datasource getter return the nullable type (`Boolean?`) instead of defaulting the absent case to `false`, and only emit when non-null.
- Every datasource method that reads a cache for `cached()` must actually read from DB/prefs — never reuse the remote-fetch method for the "cached" branch (that silently turns "cache-then-remote" into "remote-then-remote").

Any new persisted marker (prefs bucket or DB table) must be cleared on logout: implement `LogOutAction` on the `Common<X>DataSource`/`<X>DataSourceImpl` class (`override suspend fun doOnLogOut() { preferences.clear(); xDao.clear() }`) and bind it in the feature's data DI module as `binds arrayOf(<X>DataSource::class, LogOutAction::class)` — never `bind` alone, or the logout hook silently never fires.

## Dependency Injection (Koin)

- Presentation: `di/<Feature>Di.kt` (commonMain) declares `internal expect fun platform<Feature>DiModule(): Module` and a public `<feature>PresentationModule()`; actuals in `androidMain` (`viewModelOf`) and `iosMain` (`factoryOf` + `@UsedInSwift` accessors). ViewModel screen arguments are passed with `@InjectedParam` (`org.koin.core.annotation.InjectedParam`) on the constructor parameter + `parametersOf(...)` at the call site (`koinViewModel { parametersOf(id) }` on Android, `KoinPlatform.getKoin().get(parameters = { parametersOf(id) })` on iOS). **When a parameter is annotated with `@InjectedParam`, always keep `viewModelOf(::FooViewModel)` / `factoryOf(::FooViewModel)` in the DI module — never switch to the manual lambda form `viewModel { FooViewModel(it.get(), get(), ...) }`. Koin resolves `@InjectedParam` fields automatically from the `parametersOf` block.**
- Domain: `<feature>DomainModule()` in `domain/impl`. Data: `<feature>DataModule()` in `data`.
- Per-feature aggregation: `shared/src/commonMain/kotlin/me/bookk/di/feature/<Feature>DI.kt` includes presentation + data + domain modules; installed in `shared/.../di/DISetup.kt`.
- New feature module set: add to `settings.gradle.kts`, `shared/build.gradle.kts` deps, create the `<Feature>DI.kt` aggregator, add the feature's `StateFactory` to `shared/.../presentation/StateFactoryCreator.kt`, and implement it in both `iosApp/iosApp/Core/IOSStateFactoryCreator.swift` and the Android creator. Prefer the scaffold script: `cd feature/.template && ./feature.template.sh <feature_name> <FeatureNameCapitalized>`.

## Commands

- **Build Android**: `./gradlew :androidApp:assembleDevDebug`
- **Compile shared for iOS (quick check of commonMain/iosMain code)**: `./gradlew :shared:compileKotlinIosArm64`
- **Run Tests**: `./gradlew test` (runs common and Android tests)
- **Lint**: `./gradlew detekt` (if configured)

## API Documentation

Current REST API specs are served by the local backend instance:

```
http://localhost/api/{feature_name}/internal/swagger/documentation.yaml
```

`{feature_name}` can be the short or long form of the feature, e.g. `auth` or `authorization`, `appointments`, `clients`, `services`, `business`, etc.

**Before reading any endpoint contract**, fetch the relevant YAML to get the authoritative, up-to-date schema. If the URL is not reachable, **stop and return an error**: the local backend is not running and the task cannot be completed safely without current API docs.

## AI Agent Interaction Rules

- **Use the newest screen as reference** (currently `BusinessPlugins`). Never copy from screens that pass `InitData` into state factories — that pattern is deprecated.
- **A "new screen" task is not done until both platforms are wired**: commonMain state/VM/destinations, Android state/screen/nav/DI/factory, iOS Kotlin DI accessor, Swift state/screen, Swift factory method, and navigation registration on both platforms.
- **Respect layer boundaries**: Ktor models, SQL, platform APIs stay in `data`; `presentation` only sees use cases and domain entities; ViewModels never touch data sources directly.
- **Minimize cross-feature domain dependencies in `presentation`**: a feature's `presentation` module should depend only on its own feature's `domain/api` (plus shared modules like `core`, `designsystem`). If a ViewModel needs behavior that actually lives in another feature's domain, do not add that other feature's `domain/api` as a dependency of `presentation` to call it directly. Instead add a wrapper use case to the owning feature's own `domain/api` (implemented in its `domain/impl`, which depends on the other feature's `domain/api` internally and maps its errors onto the wrapper's own `Error` type) — e.g. `business`'s `JoinBusiness` wraps `employees`'s `RedeemEmployeeInvitation` so `feature/business/presentation` never needs `feature.employees.domain.api` as a dependency.
- **UDF integrity**: UI reads only `State` interfaces; user actions flow through state callbacks set in `setup()`; navigation flows through `NavigationState.push(...)` and is observed at the edge (`ObserveNavigation` / `.handleNavigation`).
- **Persistence**: use `library/cache` for simple key-value storage or Room for complex data.
- **Mocking**: for "mock" build variants provide `RoutingMock` implementations or Ktor `MockEngine`.
- **No string literals in screens**: never hardcode user-visible strings in Compose/SwiftUI screen files. All strings must be defined in the feature's `moko-resources/base/strings.xml`, accessed in Kotlin via `FeatureRes.strings.key.desc()` and rendered in Compose with `.localized()` / in Swift with `.localized()`. Dynamic strings with runtime values use the `.format(vararg args)` extension (e.g. `AppointmentsRes.strings.appointments_create_subtotal.format(count)`).
- **Never comment code**: do not add `//`, `/* */`, or `/** KDoc */` comments to Kotlin or Swift source. Code must be self-explanatory through clear naming, small functions, and the existing architectural patterns — if a piece of logic needs a comment to be understood, restructure or rename it instead. This applies to new code and to edits of existing code; do not add comments to files you touch even to explain a change. Pre-existing comments in files you edit may be left as-is unless the user asks for them to be removed.
- **No color resolution in screens**: when a state field needs a status-dependent color (a pill, a label, an icon tint), put a `me.bookk.designsystem.resources.color.ColorToken` on the state/item, computed in the ViewModel (or a `private fun <DomainEnum>.color(): ColorToken` beside the state, e.g. `AppointmentDetailsState.kt`'s `UIAppointmentStatus`) — never branch on the domain enum inside the Compose screen or SwiftUI view to pick a `Color`. Resolve the token to a themed color with the existing mapping only: `ColorToken.themed` (`@Composable` property, `me.bookk.designsystem.resources.color.ColorResolver.kt`) on Android, `ColorToken.color` (`iosApp/iosApp/DesignSystem/Colors+DesignColor.swift`) on iOS. Do not write a new per-screen `when`/`switch` over `ColorToken`.

## Design System – Screen Building Blocks

Use this section to map a screenshot or wireframe to concrete components and state types.

### Screen Shell

```
┌─────────────────────────────────┐
│ ←   Title                [Act] │  AppTopBar (TopBarSize.SMALL)
├─────────────────────────────────┤
│ content                         │  Scaffold { paddingValues -> ... }
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ ←                        [Act] │
│ Large Title                     │  AppTopBar (TopBarSize.LARGE)
│ subtitle text                   │
├─────────────────────────────────┤
│ content (collapses bar on scroll│  CollapsingAppBarScaffold { behaviour -> AppTopBar(..., behaviour) }
└─────────────────────────────────┘
```

- `AppTopBar(state)` — back arrow from `state.onBackClick`; action buttons (icon or text) from `state.actions`; `TopBarSize.SMALL` = `CenterAlignedTopAppBar`, `LARGE` = `MediumTopAppBar` + optional subtitle below.
- Standard content: `Column(Modifier.verticalScroll(...).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp))`.

### Input Fields

```
 Label text                        ← Header (section label above a group)
┌─────────────────────────────────┐
│ placeholder / value             │  TextField(state)
└─────────────────────────────────┘
  supporting / error text

┌────────────────────────── [🔒] ─┐
│ ••••••••                        │  SecureTextField(state)  (password toggle on right)
└─────────────────────────────────┘

┌──────────────────────── [▾] ────┐
│ picked value or placeholder     │  PickerField<T>(state)  type=BOTTOM_SHEET  (chevron-down icon)
└─────────────────────────────────┘

┌──────────────────────── [›] ────┐
│ picked value or placeholder     │  PickerField<T>(state)  type=SCREEN  (chevron-right → opens screen)
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ picked date                     │  DatePickerField(state)  (tap → material date picker dialog)
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ picked time                     │  TimePickerField(state)  (tap → time picker dialog)
└─────────────────────────────────┘
```

`TextField` state keys: `label`, `placeholder`, `text`, `suffix`, `startIcon`, `endIcon`, `supportingTextRes`, `validationState` (`DEFAULT`/`WARNING`/`ERROR`), `inputType` (`TEXT`/`DIGIT`/`DECIMAL`/`PHONE`/`EMAIL`/`ASCII`/`PASSWORD`), `isValid`, `enabled`, `readOnly`, `maxLength`, `onTextChanged`.

### Multi-item Picker

```
 Section Header
╔═════════════════════════════════╗
║ Selected Item 1           [–]  ║  AppCard wrapping the list
║─────────────────────────────────║
║ Selected Item 2           [–]  ║
║─────────────────────────────────║
║ + Add item                      ║  TextButton (active color, visible when isEditable)
╚═════════════════════════════════╝
```

Two variants:
- `MultiPicker<T>(state, pickerContent = { /* e.g. SelectorBottomSheet */ }) { item, onRemove -> }` — `state.addItemButton` drives the add button; tapping it sets `state.isPickerVisible = true`; the `pickerContent` lambda is shown when `isPickerVisible`; item lambda renders each row inside the `AppCard`. Use when items come from a source screen or custom picker.
- `OptionsMultiPicker<T>(state) { item, onRemove -> }` — manages its own `SelectorBottomSheet` using `state.options`; the add button label comes from `state.addItemText`. Use when options are loaded into state directly.

### Toggle / Selection

```
Label text                  [ ● ]   StateSwitch(state, onCheckedChange)  (green when on)

[✓] Label text                      CheckBox(state)
    supporting / error text

 Label text                  [✓]    CheckBoxSelector(state)  (check icon appears on right when selected)

 Label text               ●         RadioButton(state)  (circle on right; selected = filled dot)
```

### Read-only Display

```
 Section Title                      Header(text)  — titleMedium, header color, h-padding 16dp

 title label (secondary)
 value text (primary)               InfoSection(InfoLine)  — optionally clickable; HorizontalDivider below
 ─────────────────────────

 Row label                  ›       SectionItem(text, onClick)  — 56dp row, chevron-right

 ████░░░░░░░░░░░░░░░        AnimatedLinearProgress(progress: Float 0..1)  — 4dp bar
```

### Lists

```
  [loading bar]                      List(state) when isInitialLoading
  item row                           List(state) { item -> } — LazyColumn; auto shows EmptyStateView
  item row
  ...
```

- `PullToRefresh(state) { List(...) }` — pull-to-refresh wrapper; `state.isRefreshing`, `state.onRefresh`.
- `EmptyStateView(state)` — centered illustration + label; used automatically inside `List` when `state.emptyState != null`.

### Cards

```
╔═════════════════════════════════╗
║  arbitrary content              ║  AppCard { content }  — elevated bg, drop shadow, medium shape
╚═════════════════════════════════╝
```

### Overlays

**Bottom sheet (generic):**
```
▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
╔═════════════════════════════════╗
║ Optional Sheet Title            ║  DesignSystemBottomSheet(sheetState, title) { content }
║  content items 16dp apart       ║
╚═════════════════════════════════╝
```

**Selector bottom sheet:**
```
╔═════════════════════════════════╗
║ Picker Title                    ║  SelectorBottomSheet<T>(sheetState, title, data, ...)
║ ┌─────────────────────────────┐ ║
║ │ [icon] Option 1         [✓] │ ║  StandardSelectorItem  (56dp, check on right if selected)
║ └─────────────────────────────┘ ║
║ ┌─────────────────────────────┐ ║
║ │ [icon] Option 2             │ ║
║ └─────────────────────────────┘ ║
║ ╔═════════════════════════════╗ ║  ActionButton (only when requireConfirmation = true)
║ ║      Continue               ║ ║
║ ╚═════════════════════════════╝ ║
╚═════════════════════════════════╝
```

`StandardElevatedSelectorItem` — same but 72dp, `ElevatedCard`.

**Dialog:**
```
     ╔══════════════════════╗
     ║ Title (headlineSmall)║  AppDialog(title, subtitle, buttonDescriptors, onDismiss)
     ║ subtitle body text   ║    max width 312dp
     ║ [optional content]   ║
     ║        [Cancel] [OK] ║  buttons: ActionType.CANCEL / NEGATIVE / POSITIVE
     ╚══════════════════════╝
```

`AppDialogContainer` / `AppDialogScreenContainer` — bare Card wrappers for custom dialog layouts.

### Buttons

```
╔═════════════════════════════════╗  ActionButton(state)  — filled, buttonActive bg, min 48dp
║           Label                 ║
╚═════════════════════════════════╝

           Label                     TextButton(state)  — transparent bg, actionText color

  ○  ◉  (loading spinner)            Both: state.isLoading replaces text with CircularProgressIndicator
```

### Typography Quick Reference

| Style | Usage |
|---|---|
| `headlineLarge` | Large app bar title |
| `headlineSmall` | Dialog title |
| `titleMedium` | Section header, SectionItem row, strong inline label |
| `titleSmall` | MultiPicker "add" button |
| `bodyLarge` | Body text, text field value, button label |
| `bodyMedium` | Dialog subtitle, subtitles |
| `bodySmall` | Field supporting text, app bar subtitle |
| `labelMedium` / `labelSmall` | Small labels, validation messages |

Color modifiers: `.primary()` = `primaryText`, `.secondary()` = `secondaryText`, `.active()` = `actionText`, `.error()` = `error`.

### Color Tokens (`LocalColors.current`)

| Token | Role |
|---|---|
| `background` | Screen & top bar background |
| `elevated` | Card / text field container |
| `primaryText` | Main readable text |
| `secondaryText` | Hints, labels, trailing icons |
| `actionText` | Tappable text, focus/active accent |
| `header` | `Header` component text |
| `divider` | Thin separator lines |
| `success` | Switch checked track |
| `error` | Error state, negative action |
| `buttonActive` / `buttonInactive` | `ActionButton` enabled / disabled bg |
| `hintText` | TextField placeholder |

### State Interfaces Cheat-Sheet (commonMain)

| Interface | Key fields |
|---|---|
| `AppBarState` | `title`, `subtitle`, `onBackClick`, `size: TopBarSize`, `actions: ListState<AppBarAction>` |
| `ButtonState` | `icon`, `text`, `isEnabled`, `isLoading`, `onClick` |
| `TextFieldState` | `label`, `placeholder`, `text`, `suffix`, `startIcon`, `endIcon`, `supportingTextRes`, `validationState`, `inputType`, `isValid`, `enabled`, `readOnly`, `maxLength`, `onTextChanged` |
| `PickerFieldState<T>` | `textField`, `options`, `selectedItem`, `onItemPicked`, `pickerTitle`, `pickerType` |
| `MultiPickerState<T>` | `pickerTitle`, `placeholder`, `selectedItems`, `onItemsPicked`, `onItemsRemoveRequested`, `addItemButton: ButtonState`, `isEditable`, `isPickerVisible` |
| `OptionsMultiPickerState<T>` | `pickerTitle`, `options`, `selectedItems`, `onItemsPicked`, `onItemsRemoveRequested`, `addItemText`, `isEditable` |
| `DatePickerFieldState` | `textField: TextFieldState`, `datePicker: DatePickerState` |
| `DatePickerState` | `isDatePickerVisible`, `pickedDate: LocalDate?`, `minDate`, `maxDate`, `onDatePicked` |
| `TimePickerFieldState` | `textField: TextFieldState`, `timePicker: TimePickerState` |
| `TimePickerState` | `isTimePickerVisible`, `pickedTime: LocalTime?`, `minTime`, `maxTime`, `onTimePicked` |
| `DateRangePickerState` | `title`, `startDate: DatePickerFieldState`, `endDate: DatePickerFieldState`, `onDateRangeSelected` |
| `DateTimePickerState` | `isDatePickerVisible`, `pickedDate: LocalDateTime?`, `minDate`, `maxDate`, `onDatePicked` |
| `MoneyFieldState` | `currentValue: Money`, `textState: TextFieldState`, `currency: TextIcon?` |
| `BooleanState` | `text`, `isEnabled`, `isChecked`, `isValid`, `validationState`, `supportingTextRes`, `onCheckedChange` — used for both `StateSwitch` and `CheckBox`/`CheckBoxSelector` |
| `RadioButtonState` | `text`, `isSelected`, `isEnabled`, `onClick` |
| `ListState<T>` | `items`, `isInitialLoading`, `emptyState`, `loadMore` |
| `RefreshState` | `isRefreshing`, `onRefresh` |
| `InfoLine` | `id: String`, `title: StringDesc`, `value: StringDesc`, `onClick?` |
| `OptionalInfoLine` | `title: StringDesc`, `value: StringDesc?`, `onClick?` (auto-generates `id`) |
| `EmptyState` | `image: ImageResource`, `label: StringDesc`, `refreshAction: (() -> Unit)?` |


# Testing Rules

These rules govern how you write and maintain tests in this mobile repository (KMM shared layer, Jetpack Compose on Android, SwiftUI on iOS). They are non-negotiable unless I explicitly tell you to deviate for a specific task.

**Scope:** We test **logic only**. There is no UI testing in this repo for now — no Compose `composeTestRule`/Robolectric, no XCUITest/ViewInspector, no snapshot tests. Do not add UI, instrumentation, or snapshot tests, and do not add their dependencies. If a piece of behavior feels like it needs a UI test to verify, that is a signal the logic should be moved into a testable layer (see §2) — move it, don't test the UI.

## 1. Test-Driven Development (default workflow)

Implement every feature and bug fix using strict TDD:

1. **Red** — Write a failing test that captures the expected behavior. Show the failing output (or name the assertion that fails) before writing implementation.
2. **Green** — Write the minimum production code needed to make the test pass.
3. **Refactor** — Improve implementation and tests while all tests stay green.

Hard rules:
- Never write production code without a failing test that demands it.
- One behavior per test. Tests are named for the behavior they assert (`returns X when Y`), not for the method they call.
- After each step, run the relevant suite and confirm the expected pass/fail state before continuing.
- For a bug fix, first write a test that reproduces the bug (red), then fix it (green). The reproduction test is the proof.

## 2. What to test — logic only

All testable behavior lives below the UI. Push decision-making out of composables/views into state holders and the shared layer, then test it there.

**Always unit test:**
- **Every use case implementation, by default.** Each `<Name>Impl` in `domain/impl` gets a `<Name>ImplTest.kt` as part of creating or changing it — not something the user needs to ask for separately. See `## Use Case (operation) Creation` for where the test file lives.
- Business/domain logic in the KMM shared layer.
- State holders / ViewModels / presenters: given events/input, assert emitted state. Test the state object, never the view that renders it.
- Pure functions: validation, mapping, formatting, locale-aware logic (Polish formatting, currency, first-day-of-week, `IOSTextFieldState`/validation), date/time math with `kotlinx-datetime`.

**Never test (out of scope):**
- Anything that requires rendering, a UI test runtime, or a device/simulator: composables, SwiftUI views, navigation wiring, layout, visual appearance.
- Trivial mappers or one-line passthroughs with no logic.
- Framework behavior, language features, or third-party libraries.

**Guiding principle:** the view is a thin projection of state. A composable or SwiftUI view should contain no logic worth testing. If you can't fully verify a feature through state-holder and shared-layer tests, the architecture is wrong for this feature — refactor logic downward (ideally into `commonMain`) until it is testable without the UI.

## 3. Test quality

- **Behavior, not implementation.** Assert observable outcomes (returned values, emitted state, side effects), never private internals. Tests must survive refactors that preserve behavior.
- **Deterministic.** No reliance on wall-clock time, real network, random seeds, or test execution order. Inject clocks/dispatchers; use fakes.
- **Cover the edges.** Happy path + boundaries + failure modes (empty, null, invalid input, error states, concurrency where relevant). A feature isn't done until its failure cases are tested.
- **Arrange–Act–Assert** structure, with a clear single act per test.
- **Prefer fakes over mocks.** Mock only at true external boundaries (e.g. the network client). Don't assert on mock call sequences as a proxy for behavior.
- **No assertion-free tests, no tests that can't fail, no `assertTrue(true)` placeholders.** If you can't make it fail meaningfully, don't write it.

## 4. KMM / coroutines specifics

- Put tests in `commonTest`; only drop to `androidUnitTest` / `iosTest` source sets for `expect/actual` or platform-specific APIs.
- Use `runUnitTest` and `TestDispatcher` / `StandardTestDispatcher` for coroutine code; never `Thread.sleep` or real delays.
- Inject `Clock` and `Dispatchers` rather than calling `Clock.System.now()` or `Dispatchers.Default` directly, so tests stay deterministic.
- For concurrency primitives (`Mutex`, `atomicfu`), write a test that would fail under the unsynchronized version.
- When testing `expect/actual` (e.g. currency retrieval, locale first-day-of-week), assert the shared contract in `commonTest` and cover platform divergence in the platform source set.
- Keep `commonTest` fast and runnable on the JVM; these are plain unit tests, not instrumented ones.

## 5. Process rules for you (the agent)

- Run the relevant suite before declaring a task done, and report the result. Default to the affected source set during the loop; reserve the full `commonTest` run for a final check.
- If a requirement is ambiguous, encode your assumption as a test and state the assumption explicitly in your summary.
- Never delete, skip (`@Ignore` / `xit`), or weaken a failing test to make the suite green. If a test is genuinely wrong, explain why and propose the change before acting.
- Don't pad coverage with low-value tests to hit a number. Coverage is a side effect of testing real behavior, not a target.
- Do not introduce UI/instrumentation/snapshot test tooling or dependencies. If you believe a UI test is genuinely warranted, stop and raise it with me instead of adding one.
- When you add a feature, list which tests you added and what each one protects.

## 6. Platform-specific test setup (KMM unit tests)

**Test dispatcher boilerplate** — every test class that has suspend code needs:
```kotlin
private val testDispatcher = UnconfinedTestDispatcher()
@BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
@AfterTest fun tearDown() { Dispatchers.resetMain() }
```

**Fixture pattern** — use `private class Fixture { val dep = mock<Dep>(); val sut = Impl(dep) }` so a fresh SUT and fresh mocks are created for every test. Never share state between tests. Instantiate it as `val fixture = Fixture()` — never `val f = Fixture()`, `val fixture = Fixture()`, or any other name. Inside the body, the system under test is `fixture.sut(...)` and mocks are `fixture.dep`.

**Mocking library** — use **Mokkery** (`dev.mokkery`), a KMP compiler-plugin-based mock framework. Key API:
- `mock<T>()` — strict mock (throws on unstubbed calls); `mock<T>(MockMode.autofill)` for a relaxed placeholder
- `everySuspend { mock.suspendFn(any()) } returns value` — stub suspend functions
- `everySuspend { mock.suspendFn(any()) } returns Unit` — stub void suspend (replaces `coJustRun`)
- `everySuspend { mock.suspendFn(any()) } throws error` — stub suspend throws
- `every { mock.fn(any()) } returns value` — stub non-suspend functions (e.g. Flow-returning)
- `verifySuspend { mock.suspendFn(args) }` — verify suspend call
- `verifySuspend(VerifyMode.exactly(n)) { mock.suspendFn(args) }` — exact call count
- `verifySuspend(VerifyMode.order) { mock.fn1(a); mock.fn2(b) }` — ordered call sequence
- `any()` — any-argument matcher (from `dev.mokkery.matcher`)
- `matches({ "desc" }) { predicate }` — predicate matcher (replaces MockK's `match {}`)

**Fixture files** — when multiple test classes in a module share the same stub helpers, extract them into an `internal` top-level file (e.g. `AppointmentTestFixtures.kt`, `ServiceTestFixtures.kt`, `BusinessTestFixtures.kt`) in the `commonTest` source set at the module's root package. Sub-package test files import via `import me.bookk.feature.<name>.domain.impl.stubX`.

**Given/When/Then** — every `@Test` function **must** use `runUnitTest` (from `me.bookk.core.test`) and call `given()`, `whenn()`, and `then()` as structural markers. `runUnitTest` enforces their presence at runtime. The three markers divide the test body into: setup & mock-stubbing (`given`), the single action under test (`whenn`), and assertions & verifications (`then`). Example:
```kotlin
@Test
fun `returns created business`() = runUnitTest {
    given()
    val fixture = Fixture()
    everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()

    whenn()
    val result = fixture.sut("My Salon")

    then()
    assertEquals(stubBusiness(), result)
}
```
Import: `import me.bookk.core.test.given`, `import me.bookk.core.test.whenn`, `import me.bookk.core.test.then`, `import me.bookk.core.test.runUnitTest`. The `testFixtures` of `:core` are added to `commonTest` by the convention plugin automatically.

**SharedFlow event tests** — package-level `MutableSharedFlow` instances (`appointmentEvents`, `clientEvents`, `serviceEvents`, `serviceGroupEvents`) require `Dispatchers.Unconfined` on the subscriber coroutine for the event to be delivered synchronously during `emit()`. With `UnconfinedTestDispatcher`, the emitter queues the subscriber continuation in the test scheduler but doesn't run it until the test coroutine suspends — if `job.cancel()` happens before that suspension, the event is lost. Pattern that works (with given/when/then):
```kotlin
given()
val fixture = Fixture()
everySuspend { fixture.dataSource.doThing(any()) } returns result
val events = mutableListOf<SomeEvent>()
val job = launch(Dispatchers.Unconfined) { someEvents.collect { events.add(it) } }

whenn()
fixture.sut(args)

then()
job.cancel()
assertTrue(events.any { it is SomeEvent.Created })
```

**`invoke()` as operator** — some use case interfaces declare `suspend fun invoke(...)` without the `operator` modifier. These cannot be called with `fixture.sut(args)` shorthand; use `fixture.sut.invoke(args)` in tests.

**`PassKeyManager.Error.Unknown` constructor** — takes a required `cause: Throwable?` parameter. Throw it in tests as `PassKeyManager.Error.Unknown(null)`.

