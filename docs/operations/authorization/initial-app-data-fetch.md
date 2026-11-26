[← Operations](../README.md)

# Initial app data fetch (and background data fetch)

`InitialAppDataFetch.rawFetch()` / `.timestampProtectedFetch()`
· `rawFetch` is called from [Sign in](sign-in.md), [Sign in with Telegram](sign-in-with-telegram.md) and
[Finish sign-up](finish-sign-up.md). `timestampProtectedFetch` is called from `BootstrapViewModel` on every app start.

It warms the cache so the first screens can render locally. Like [Log out](log-out.md), it does not call other
features directly: features take part by implementing a trigger interface from `core:domain`
(`me.bookk.core.domain.fetch`) and binding it in their domain Koin module with
`binds arrayOf(<UseCase>::class, <Trigger>::class)`. `InitialAppDataFetchImpl` receives each trigger as a
`getAll()` list.

| Trigger | When it runs | Bound implementations |
|---|---|---|
| `AppStartAction.doOnAppStart()` | `timestampProtectedFetch` only, on every call, before the throttle and authorization check | [`DeleteExpiredPersonalDataExportFile`](../settings/delete-expired-personal-data-export-file.md), [`DeleteExpiredBusinessDataExportFiles`](../business/delete-expired-business-data-export-files.md) |
| `DataFetchAction.doOnDataFetch(isSignIn)` | Authorized, after the profile update; awaited by the caller | [`RefreshBusinessInfo`](../business/refresh-business-info.md) (`applyDashboardIdFromRemote = isSignIn`) |
| `BackgroundDataFetchAction.doOnBackgroundDataFetch()` | Launched in the application scope once every data fetch has finished | [`UpdateNotificationToken`](../settings/update-notification-token.md) (uploads the stored push token again), [`GetNotificationSettings`](../settings/get-notification-settings.md), [`GetAvailablePasskeys`](../settings/delete-passkey.md) (mobile only: tells the passkey provider which passkeys the account still accepts), the business feature's `DashboardBusinessDataFetch` |
| `BusinessDataFetchAction.doOnBusinessDataFetch(businessId)` | Run by `DashboardBusinessDataFetch` for the current dashboard business | [`GetServices`](../services/get-services.md), [`GetServiceGroups`](../services/get-service-groups.md), [`GetClientsList`](../clients/get-clients-list.md), [`GetEmployees`](../employees/get-employees.md), [`GetAppointmentSettings`](../appointments/get-appointment-settings.md) |

`rawFetch` always runs and passes `isSignIn = true`, so the business refresh **adopts the server's dashboard
business selection**. `timestampProtectedFetch` passes `isSignIn = false` and keeps the local selection. It
fetches only when at least 30 minutes have passed since `authorization_prefs.last_initial_data_fetch_at`, or on
every call while the local database is not persistent (`AuthorizationDataSource.isDatabasePersistent()`, from
`DatabaseStorage`: `SELECT file FROM pragma_database_list WHERE name = 'main'` is empty for an in-memory database).
The client web app falls back to an in-memory database when it cannot get OPFS storage; the timestamp lives in
preferences and outlives that database, so without this rule the profile and other cached data would stay missing
for up to 30 minutes after a reload. For the same reason it also fetches when the last fetch was recent but the
user is signed in and `UserProfileCRUD.observe().first()` (the local `user_profile` row) is `null`: every completed
fetch saves the profile before the timestamp, so a signed-in user without one has a database that was replaced or
emptied while the preferences survived (the web app switching between the OPFS SAH pool and `OpfsDb`, which keep
separate files, or a renamed SAH pool). When due, it first calls `GetSignUpLegalDocuments.refresh()` to fetch and persist both public legal documents, then
reads `AuthorizationDataSource.getIsAuthorizedFlow().first()`. If unauthorized, it returns immediately: no profile,
data or background fetch is started, and no initial-data timestamp is saved. `rawFetch()` does not preload legal
documents and does not run app start actions.

```mermaid
flowchart TD
    A([rawFetch]) --> P
    B([timestampProtectedFetch]) --> Start[for each AppStartAction: runCatching doOnAppStart<br/>local export file cleanup]
    Start --> Persistent{isDatabasePersistent?<br/>main database file not empty}
    Persistent -- no --> Legal
    Persistent -- yes --> Due{now - last_initial_data_fetch_at >= 30 min<br/>or never fetched?}
    Due -- no --> Profile{Authorized and<br/>UserProfileCRUD.observe.first == null?}
    Profile -- no --> Skip([return])
    Profile -- yes --> Legal
    Due -- yes --> Legal[GetSignUpLegalDocuments.refresh<br/>GET /api/user/legal then persist signup_legal_documents_prefs.documents]
    Legal --> Auth[AuthorizationDataSource.getIsAuthorizedFlow.first]
    Auth --> Authorized{Authorized?}
    Authorized -- no --> PublicOnly([return after public documents only])
    Authorized -- yes --> P2
    Legal -- error --> EX
    P[UserProfileCRUD.updateFromRemote<br/>GET /api/user/me then upsert user_profile] --> DataA[for each DataFetchAction:<br/>runCatching doOnDataFetch isSignIn = true]
    P2[UserProfileCRUD.updateFromRemote] --> DataB[for each DataFetchAction:<br/>runCatching doOnDataFetch isSignIn = false]
    DataA --> Background
    DataB --> Background
    Background[launch in applicationScope, fire-and-forget:<br/>for each BackgroundDataFetchAction, one after another,<br/>runCatching doOnBackgroundDataFetch] --> Mark[authorization_prefs: last_initial_data_fetch_at := now]
    Mark --> R([return])
    P -- error --> EX([rethrow])
    P2 -- error --> EX
```

Legal preloading and the profile fetch are not wrapped in `runCatching`. A failure propagates and the
initial-data timestamp is not written. Failed legal preloading prevents the private fetches from starting;
it does not overwrite an older complete legal-document snapshot. Every trigger action runs in its own
`runCatching`, so one failure never blocks the others. Actions of the same trigger run in Koin registration
order, which is not guaranteed; nothing depends on that order.

## Background data fetch

The background actions run in the application scope, so they outlive the caller, and **one request after
another**, which is intentional to avoid a burst of requests. They start only after every `DataFetchAction` has
finished, so the business refresh has already stored the dashboard business.

`DashboardBusinessDataFetch` (an internal class in `business/domain/impl`) reads the **current** dashboard
business and runs every `BusinessDataFetchAction` for it. It does not wait for a later one, because the
dashboard flow never completes and waiting would hold up the remaining background actions:

```mermaid
flowchart TD
    Start([doOnBackgroundDataFetch]) --> Current[ObserveDashboardBusinessChanges.first]
    Current -- null --> End([return])
    Current -- business --> Loop[for each BusinessDataFetchAction, one after another:<br/>runCatching doOnBusinessDataFetch businessId]
    Loop --> S[GetServices.refresh]
    Loop --> SG[GetServiceGroups.refresh]
    Loop --> C[GetClientsList.refresh]
    Loop --> E[GetEmployees.refresh]
    Loop --> AS[GetAppointmentSettings.refresh]
    S & SG & C & E & AS --> Done([done, errors swallowed per action])
```

The appointments plugin flag is not part of this fetch: `RefreshBusinessInfo` already refreshes it for every
business during the data fetch.
