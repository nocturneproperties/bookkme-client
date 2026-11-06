# Mobile API integration and UI plan

Status: Steps 1–3 reviewed and pushed. Step 6 is implemented and validated, awaiting user review. Step 4 is implemented and validated, awaiting user review. Step 5 remains pending. Step 7 is skipped for now at the user's request. Step 8 is excluded from the mobile app's scope. Step 9 is complete. Step 10 is approved. Step 4 awaits review before Step 5 starts.

Execution order updated at the user's request: proceed with Step 6 before the export UI in Steps 4–5.

Source: `GET http://localhost/api/monolith/internal/swagger/documentation.yaml`, fetched 2026-11-04.
The current schema was compared with the schema fetched during the preceding UI-gap review and with
the mobile data, domain and presentation code.

## Review checkpoints

Complete one numbered step at a time. Each step produces a reviewable diff, validation results and a
short description of behavior. Stop for the user's evaluation before starting the next step. Do not
combine later UI work into Step 1.

All new screens must include Android and iOS implementations and navigation. All new or changed
strings must include English, Russian, Ukrainian and Polish. Follow the project's TDD rules for
behavior changes and update operation/database documentation where required.

## Confirmed API changes

| Area | Previous contract | Current contract |
|---|---|---|
| Confirmation endpoint | `POST /api/auth/passkey/step_up` | `POST /api/auth/passkey/confirm_action` |
| Confirmation models | `StepUpRequest`, `StepUpToken` | `ActionConfirmationRequest`, `ActionConfirmationToken` |
| Supported actions | Client deletion and personal/business exports | Same actions, with `DELETE_ACCOUNT` appended |
| Account deletion | `VerifyDeleteAccRequest` with request id and passkey assertion | `DeleteAccountRequest` with `confirmationToken` |
| Account confirmation binding | Direct assertion at deletion | `DELETE_ACCOUNT`, nil business UUID, caller's user UUID as target |
| Account deletion rejection | Direct passkey verification errors | HTTP 403 / `ACTION_CONFIRMATION_INVALID` (12); HTTP 422 / `VERIFICATION_FAILED` (4) |
| Client deletion body | `stepUpToken` | `confirmationToken` |
| Personal/business export start bodies | `stepUpToken` | `confirmationToken` |
| Client/business confirmation error | `BUSINESS_STEP_UP_INVALID` (200040) | `BUSINESS_ACTION_CONFIRMATION_INVALID` (200040) |
| Personal export confirmation error | `USER_STEP_UP_INVALID` (100005) | `USER_ACTION_CONFIRMATION_INVALID` (100005) |
| Notification update schema | `NotificationSettings.Update` | `NotificationSettingsUpdate`; properties are unchanged |

Confirmation still obtains a fresh sign-in challenge, verifies a passkey and returns a single-use token
bound to the action, business and target. The documented lifetime remains two minutes. The request
properties remain `requestId`, `publicKeyCredentialJson`, `action`, `businessId` and `targetId`.

The notification schema rename does not require a behavior change or a different wire payload.
No other endpoint or existing shared component schema changes were found in this comparison.

## Work sequence

### 1. Integrate the latest backend changes

- Replace the StepUp route and remote model names with ActionConfirmation names.
- Rename token fields and error constants across authorization, settings, business and clients.
- Append `DELETE_ACCOUNT` without changing the existing action enum order.
- Compose account deletion with the shared confirmation use case: resolve the caller's id, confirm
  `DELETE_ACCOUNT` with a nil business UUID, then send only `confirmationToken` to the delete endpoint.
- Map confirmation failures and deletion errors 12 and 4 to the existing account-verification UI
  behavior. Preserve silent user cancellation and propagate unrelated failures.
- Preserve best-effort credential-provider cleanup after successful account deletion. The shared
  confirmation path must retain the verified credential information needed for that cleanup without
  asking for a second passkey assertion.
- Keep session/cache cleanup after successful remote deletion; do not clear it when confirmation or
  deletion fails.
- Update DI wiring, API audit status and affected operation diagrams, including composed-use-case
  entries in the operations index.

Review output: backend-integration diff and relevant tests. Regression coverage must prove the new
HTTP route and serialized request bodies, action enum order, account-deletion action/target binding,
error handling, cancellation, cleanup ordering and absence of cleanup after failures. Compile the
affected shared code for both platforms. No authenticated destructive request is needed for validation.

#### Step 1 review output

Implemented:

- Authorization now posts `ActionConfirmationRequestRemote` to `/api/auth/passkey/confirm_action` and
  reads `ActionConfirmationTokenRemote`. The datasource request is named `ActionConfirmationRequest`.
- Client deletion and both export starts send `confirmationToken` with their existing field tag 1.
- The action enum appends `DELETE_ACCOUNT` at ordinal 3, preserving ordinals 0–2.
- Account deletion resolves the caller id, confirms the action with `Uuid.NIL` as business UUID, and sends
  the token-only `DeleteAccountRequestRemote`. The previous direct-assertion request model is removed.
- Review refinements replace nil UUID parsing with `Uuid.NIL` in personal export and account deletion,
  including their tests. `DeleteAccountImpl.invoke()` now delegates to `confirmAccountDeletion`,
  `deleteAccountOnRemote`, `signalAccountDeleted` and `clearSession`; ordering and behavior are unchanged.
- `ConfirmSensitiveAction` returns an in-memory `ActionConfirmation` containing the token and verified
  assertion JSON. Account deletion reuses that assertion for best-effort credential removal signaling
  after the remote DELETE succeeds. Other callers consume only the token.
- Confirmation failures and deletion errors 12/4 preserve the existing account-verification UI error.
  Cancellation and unrelated errors propagate. Failure does not run logout/cache cleanup or clear the
  session. Existing Koin constructor factories resolve the added dependencies without manual wiring.
- API audit, affected operation diagrams, composed-use-case index and profile-read documentation are
  updated. No UI, persistence schema, preference key or logout-hook binding changes were needed.

Validation:

- Red: six contract assertions failed before implementation, covering the obsolete route, missing
  action, old account-deletion fields and old token field names in three feature payloads. Domain tests
  also failed to compile before the new result type and confirmation composition existed.
- Green: all 384 tests across the affected authorization/settings/business/clients data and domain
  suites pass (`testAndroidHostTest` on each of the eight modules).
- Follow-up review validation: all 74 settings domain tests pass after the `Uuid.NIL` change; all 18
  account-deletion tests and the file formatting check pass after extracting helper methods.
- `:androidApp:assembleDevDebug` passes.
- `:shared:compileKotlinIosArm64` passes.
- All five changed operation diagrams parse with Mermaid.
- Spotless passes for all Step 1 Kotlin files; `git diff --check` passes. Repository-wide Spotless still
  fails on pre-existing formatting in presentation tests; those files are unchanged by this step.
- HTTP validation uses `MockEngine`. No live account/client deletion or export was performed.

Ten tests were added, alongside updated existing regressions:

| Test file | Added protection |
|---|---|
| `SensitiveActionHttpTest` | Actual DELETE method, account route and token-only protobuf bytes, checked against an independent expected body model |
| `ConfirmedOperationsContractTest` in authorization data | Replacement account-deletion message field/tag contract; existing tests also pin ActionConfirmation models and appended enum order |
| `ConfirmSensitiveActionImplTest` | Missing native credentials, unrelated native failures and unknown confirmation errors; existing tests additionally verify retention of the exact assertion |
| `DeleteAccountImplTest` | Action/target/token binding and cleanup order, error 12, no destructive work after failed confirmation, unrelated confirmation failures and deletion network failures |

Existing expiry and passkey ownership tests now inject those errors at the shared confirmation stage,
matching the current backend. Credential cleanup tests assert exact assertion content rather than payload
object identity, because the result crosses a domain boundary before the platform payload is reconstructed.

Step 1 was reviewed; the user authorized Step 2.

### 2. Add legal acceptance to signup

Load current legal versions and links, collect explicit acceptance, and submit the accepted version
identifiers. Handle documents changing during signup and avoid blocking signup when no documents
are published. Keep cross-feature access behind authorization-owned use cases.

Review output: Android/iOS signup flow, translations and ViewModel/use-case tests.

#### Step 2 review output

Implemented:

- Signup loads published legal documents and links through the authorization-owned
  `GetSignUpLegalDocuments` wrapper around settings `GetLegalDocumentVersions`.
- One unchecked checkbox accepts all published documents on Android and iOS. Both document names are
  underlined hyperlinks inside its label; a single published document uses its own label and link. The
  checkbox is hidden when no documents are published. Link taps open the published URL through a
  `weakVMClosure` callback and never accept documents. Signup submits both displayed version identifiers
  after the shared checkbox is explicitly checked.
- Loading and loading failures block signup. Failure is shown beneath the acceptance checkbox using
  `legalAcceptance.validationState` and `supportingTextRes`, with retry alongside it. Retry clears the
  validation error before loading again. No separate error flag is stored. A successful empty response
  allows signup with empty acceptance; a single published document requires only its acceptance.
- Both legal rejections (`LegalDocumentVersionUnknown` and `LegalDocumentsNotAccepted`) explain that
  documents changed, uncheck shared consent and reload the requirements. The user must accept again and
  retry signup explicitly. Entered profile data is preserved. Callbacks from replaced controls cannot
  accept new documents, and a failed reload cannot reuse stale consent.
- Signup reads the displayed document records from screen state, snapshots entered data and accepted
  versions before registration, and blocks duplicate submissions while registration is in progress.
  Consent callbacks are bound to the displayed document states, preventing old callbacks from accepting
  replacement documents without a stored revision counter. Existing success/failure haptics are preserved.
- Signup now uses parameterless state creation and `setup()` callbacks on both platforms. English,
  Russian, Ukrainian and Polish strings, operation diagrams and the API audit are updated.
- No API payload, cache schema, preference key or logout-hook change was required. Steps 3–10 are untouched.

Validation:

- Red: the new use-case and signup tests initially failed to compile against the missing legal-document
  API/state. A subsequent regression reproduced an old acceptance callback accepting a replacement
  document (`ignores acceptance callbacks from a replaced document version`, 1 failure in 29 tests).
- Green: all 132 authorization domain/presentation tests pass: 80 domain tests and 52 presentation tests,
  including 33 signup ViewModel tests and 3 new wrapper use-case tests. Review coverage verifies the shared
  label, one-click acceptance of both versions, each single-document case, and document names and URLs.
  The reading test exercises both link callbacks and verifies that neither checks the shared checkbox.
- `:androidApp:assembleDevDebug` passes.
- `:shared:compileKotlinIosArm64` passes.
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme dev -configuration Debug
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build` passes.
- Spotless formatting is applied to the changed Kotlin files; `git diff --check` passes.
- Both affected operation diagrams parse with Mermaid. All four translation files have matching keys,
  placeholders and newline markers. Each linked document name occurs exactly once in its acceptance
  label in all four languages.
- No live signup/account creation was performed; backend access was limited to fetching the Swagger
  contract. UI behavior is covered through shared state tests, without adding UI-test tooling.

Added tests (`SignUpViewModelTest` unless otherwise noted):

| Test | Protection |
|---|---|
| `requires explicit acceptance before submitting published documents` | Signup is blocked until the shared checkbox is checked. |
| `uses one acceptance label containing both linked documents` | Both document names appear in the single unchecked consent label. |
| `one checkbox accepts both published versions` | One user action supplies the exact displayed privacy and terms versions. |
| `allows signup with empty acceptance when no documents are published` | No-document installations can register. |
| `requires only the single published document` | A privacy-only response needs only privacy acceptance. |
| `requires only terms when privacy policy is not published` | A terms-only response uses its own label and version. |
| `revoking acceptance blocks signup` | Consent can be withdrawn before submission. |
| `provides each document name and published URL for its inline link` | Both acceptance labels expose the correct linked document name and server URL. |
| `opens the published document link without accepting it` | Reading uses the server URL and does not imply consent. |
| `blocks signup while legal documents are loading` | In-flight requirements cannot be bypassed. |
| `keeps signup blocked after document loading fails` | Failure sets the acceptance validation state and supporting text and cannot bypass consent. |
| `retries a failed document load` | A successful retry clears acceptance validation and recovers the form. |
| `clears the acceptance error while retrying document loading` | Retry immediately clears the supporting text and validation error while keeping signup blocked. |
| `requires renewed consent after either legal rejection` | Both backend legal errors clear consent, refresh and explain recovery. |
| `submits refreshed versions only after accepting again` | A second explicit attempt submits current identifiers. |
| `blocks stale consent when reloading after rejection fails` | Old acceptance cannot bypass failed refresh. |
| `does not start a second signup while one is in progress` | Field edits cannot re-enable concurrent submission. |
| `ignores acceptance callbacks from a replaced document version` | Retained callbacks cannot accept replacement documents. |
| `GetSignUpLegalDocumentsImplTest`: `returns published versions and links in authorization owned records` | Both document kinds preserve their version and URL across the feature boundary. |
| `GetSignUpLegalDocumentsImplTest`: `returns no requirements when no documents are published` | The wrapper preserves a successful empty response. |
| `GetSignUpLegalDocumentsImplTest`: `propagates loading failures instead of treating them as no documents` | The wrapper cannot silently bypass requirements on failure. |

The previous generic-legal-error test is replaced by the recovery tests required by this step.
Step 2 was reviewed; the user authorized Step 3.

### 3. Add legal review and renewed acceptance in Settings

Replace Android's Google placeholder links and iOS's Contact Us destinations with current legal
document links. Show acceptance status and allow acceptance of newly effective versions.

Review output: Android/iOS Settings behavior and state tests.

#### Step 3 review output

Implemented:

- Settings has a Legal section on Android and iOS. It replaces Android's placeholder links and iOS's
  Contact Us destinations with the current published document links supplied by `GetLegalDocumentStatuses`.
- Each published document has a compact Settings row with its name, accepted/review-needed status and
  an external-link indicator. Raw version identifiers stay out of the visible layout; the exact version
  remains bound to the record submitted through `AcceptLegalDocuments`. Opening a link only reads the
  document. Already accepted versions are omitted from the explicit acceptance action.
- Pending documents show a short consent explanation beside an “Accept documents” button. Accepted
  documents use a quieter status color resolved from shared state. Stable document identities keep
  rows consistent when data is refreshed; both platforms support wrapping text and full-row link taps.
- A successful save plays a success haptic and reloads server status. A newer version returned by that
  reload remains pending and needs another explicit acceptance. Unknown-version rejection explains the
  change and reloads without submitting replacement versions automatically.
- Loading and failure states block acceptance. Failure provides inline retry; a successful empty response
  shows that no documents are published. Refresh keeps document rows visible with a small progress
  indicator while invalidating consent callbacks and disabling acceptance. Failed reads discard the
  stale rows and offer inline retry. Duplicate submissions and overlapping status refreshes are blocked.
- All new callbacks use weak ViewModel references. English, Russian, Ukrainian and Polish strings and
  API/operation documentation are updated. Existing Koin constructor registrations resolve the added
  use cases on both platforms.
- No new screen, route, use case, remote payload, database schema, preference key or logout binding was
  required. Steps 4–10 are untouched.

Validation:

- Red: the new shared-state tests failed to compile before implementation because the Settings legal
  state and ViewModel dependencies did not exist.
- Green: all 84 Settings presentation tests pass, including 32 dashboard tests (23 added legal
  regressions replacing the old generic placeholder-link test).
- The UX refinement added failing tests for consent guidance, shared status styling
  and preserving rows while blocking stale acceptance during refresh, plus accepted-state coverage.
- `:androidApp:assembleDevDebug` passes.
- `:shared:compileKotlinIosArm64` passes.
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme dev -configuration Debug
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build` passes.
- Spotless is applied to changed Kotlin files; `git diff --check` passes. All four translation files
  have matching keys, placeholders and newline markers.
- Backend access was limited to fetching the current Swagger contract. No live legal acceptance was
  submitted. UI behavior is verified through shared state tests; no UI-test tooling was added.
- Operation call graphs are unchanged; their documentation now records the Settings entry points.

Added tests in `SettingsDashboardViewModelTest`:

| Test | Protection |
|---|---|
| `shows current versions and acceptance status` | Both document versions and accepted/pending labels match server records. |
| `opens the published privacy URL without accepting` | Privacy reading opens the exact URL and never records consent. |
| `opens the published terms URL without accepting` | Terms reading opens the exact URL and never records consent. |
| `submits only pending displayed versions` | Already accepted versions are omitted. |
| `submits both pending versions` | One explicit acceptance submits both displayed pending versions. |
| `accepts a terms only publication` | A single published document needs only its own acceptance. |
| `hides acceptance when all current versions are accepted` | No redundant acceptance is offered or submitted. |
| `shows an empty message when no documents are published` | Successful empty publication is distinct from loading failure. |
| `blocks acceptance while loading` | In-flight requirements cannot be submitted. |
| `offers retry and blocks acceptance after loading failure` | Errors cannot be mistaken for an empty publication. |
| `recovers from a failed load through retry` | Retry restores current documents and acceptance. |
| `refreshes server acceptance after saving` | Status is re-read instead of assuming acceptance locally. |
| `keeps a newly published version pending after saving` | Saving an old version never accepts a replacement silently. |
| `reloads unknown versions and requires another explicit acceptance` | Rejection reloads without automatically resubmitting. |
| `ignores acceptance callbacks for replaced documents` | Retained callbacks cannot submit old displayed versions. |
| `prevents duplicate acceptance and overlapping refresh` | Concurrent taps and lifecycle callbacks do not race saves and reads. |
| `plays success haptic when legal acceptance is saved` | Success follows a confirmed server write. |
| `does not play success haptic when legal acceptance fails` | Failed writes preserve pending state and report errors. |
| `discards stale status when post acceptance refresh fails` | Failed confirmation reads require retry before another submission. |
| `explains explicit acceptance beside pending documents` | Pending documents explain what the explicit action means. |
| `distinguishes pending documents from quiet accepted states` | Shared state supplies status color tokens for pending and accepted documents. |
| `keeps document rows visible while refreshing and blocks stale acceptance` | Refresh preserves the reading layout while preventing submissions from stale callbacks. |
| `removes the consent explanation after all documents are accepted` | Accepted-only sections do not show an unnecessary consent prompt. |

The previous placeholder-link test is replaced by the two exact-URL/no-consent regressions.
Step 3 was reviewed and pushed. The user deferred Steps 4–5 and authorized Step 6.

### 4. Add personal data export UI

Provide request, running, ready and failed states; passkey confirmation; status refresh; cooldown and
expiry display; and platform save/share of the downloaded JSON. Handle download becoming unavailable.

Review output: complete Android/iOS export flow and state/use-case tests.

#### Step 4 review output

Status: Implemented and validated, awaiting user review.

Implemented:

- Settings → Account → Export my data opens one new screen on Android and iOS. The screen uses
  parameterless state creation, shared callbacks with weak ViewModel references, existing buttons,
  app bars and notification handlers. Navigation, factories and DI are wired on both platforms.
- The screen observes the nullable `personal_data_export` Room row through `GetPersonalDataExport.flow()`.
  The read-only flow maps expired records to null; other records pass through unchanged. The
  ViewModel refreshes on open only when the first observed value is null (missing or expired: logout,
  reinstall, another device) or RUNNING; cached READY, NONE and FAILED are trusted, so a valid export
  opens offline without a network call. While the observed status is RUNNING it polls
  every 10 seconds, at most 30 times; repeated RUNNING writes do not restart or shorten the interval.
  Overlapping refreshes are dropped through the shared operation key, and request/download are
  disabled while a status refresh runs.
  `refresh()` always fetches and saves the exact server response through separate datasource methods,
  clearing the export file first unless the response is READY and unexpired.
  The observer alone supplies screen data; refresh return values are not rendered directly.
- Request, preparing, ready and failed states use the observed domain status. Expired exports emit null
  and show the default screen, with no synthetic NONE record or retained cooldown. While an export is
  RUNNING or READY the screen says a new export can be requested after the current one expires (the
  backend allows one start per day, but a ready file lives 72 hours and Request stays hidden until it
  expires); for NONE/FAILED a future `availableAgainAt` is shown as a localized date. Missing timestamps
  remain absent. RUNNING
  observations are polled at a fixed interval; export requests require a user action. Flow failures use
  resilient observation and inline retry.
- Request taps use the existing passkey-confirmed start operation, which saves its status to Room.
  Success plays a haptic; cancellation stays silent. Confirmation failures and rate limits are inline, with
  explicit retry or refresh. One stable launch key excludes overlapping reads, confirmations and
  downloads; presentation flags are not used as concurrency guards.
- The download use case caches successful JSON as one file,
  `personal_data_exports/<requestedAt>-<expiresAt>/bookk-personal-data.json`, and returns its path.
  Further presses reuse the file, including after screen recreation. New export requests, refreshes
  returning a non-downloadable status and logout clear the directory; saving a new file replaces the
  previous one. Other export directories are unaffected. Failed downloads remain retryable through the
  backend. JSON is not stored in Room, preferences, UI state or navigation.
  The ViewModel pushes `PersonalDataExportDestinations.Share(filePath)`. Android opens an
  `ACTION_SEND` chooser through a non-exported FileProvider scoped to `cache/personal_data_exports/`;
  iOS presents `UIActivityViewController` in a sheet, including Save to Files. No temporary copies are
  made.
- Unavailable downloads hide stale actions and offer status refresh. Other status/request/download/
  sharing errors have one inline presentation and an explicit recovery action. Shared authentication
  and access handling is preserved. The failed initial read regression also prevents a stuck
  “Checking export status” title after loading has ended.
- All 28 new strings are translated into English, Russian, Ukrainian and Polish. The request description
  now explains JSON and how the downloaded file can be used; its footer refers to the app, not its brand.
  Operation/cache documentation and the API audit are updated. Database version 25 adds the singleton
  `personal_data_export` table through `AutoMigration(24, 25)` and preserves all 27 existing tables.
  Status uses explicit columns and is cleared on logout through the datasource's added binding.
  No preferences bucket, endpoint or payload changes. The saved Step 9 schema was used per the user's
  instruction; no fresh server documentation fetch or authenticated export was needed.

Validation:

- Red: the new export tests initially failed to compile against missing state/ViewModel/file-sharing
  APIs. The Settings entry-point test failed against the missing action and destination. A recovery
  assertion then failed on the stale loading title (1 failure in 26 export tests) before its fix.
- Three download-reuse regressions initially failed because repeat presses, sharing retries and status
  updates still triggered another backend download; they pass with the private per-export JSON cache.
- Red cache coverage failed against the missing local-flow, persistence and migration APIs. Two
  same-status recovery tests failed before the observation fix; an additional datasource regression
  reproduced the equality filter dropping the persisted snapshot needed to reset retry controls.
- Green: all 275 affected tests pass: 149 Settings presentation, 91 domain, 26 data and 9 database.
  The export ViewModel now has 50 behavior tests. Added cache/use-case/migration regressions cover
  unknown versus confirmed NONE records, conditional entry refresh, database-only rendering, failure
  preservation, same-status recovery and logout cleanup.
- Android assembly (`:androidApp:assembleDevDebug`), shared iOS compilation
  (`:shared:compileKotlinIosArm64`) and the full Xcode dev simulator build pass.
- New export code and other changed Kotlin files pass scoped Spotless. The two legacy dashboard
  ViewModel/test files retain their pre-existing long-line lint violations; the new lines meet the
  formatting rules. `git diff --check` passes.
- All four locale files have matching keys, placeholders and newline markers. The five cache-related
  Mermaid diagrams parse, including status/start/logout operations and both schema diagrams. Validation uses shared-state tests and builds; no UI-test tooling, live
  export request, download or passkey confirmation was used.

Design review correction:

- The initial UI drifted from the approved concept. The screens restore neutral headings, shared
  document/success icons, the separate passkey confirmation, the ready file row and split expiry row.
  Request and ready states hide the unnecessary refresh control; error/cooldown recovery retains it.
  Per the subsequent iOS clarification, iOS uses standard inset-grouped `List`/`Section` containers,
  native row separators and section footers, and the original shared button components. Custom button
  borders, shapes, corner radii and the shared button modification have been removed. Android retains
  the concept's card layout and outlined refresh action.
- Three state regressions were red on the missing icon/footer API before implementation. Manual
  simulator previews render the production content with sample state, without authenticated calls
  or UI-test tooling. The [approved concept and rendered states](../design/personal-data-export/README.md)
  are saved for review.

Added tests in `PersonalDataExportViewModelTest`:

| Test | Protection |
|---|---|
| `keeps a cached ready export available without refreshing` | A valid READY cache stays downloadable during cooldown with no network call. |
| `refreshes on open when nothing usable is cached` | A missing or expired record is reconciled with the server. |
| `trusts cached settled statuses on open` | Cached NONE and FAILED open without a network call. |
| `refreshes on open when the cached export is running` | RUNNING is checked immediately instead of waiting for the first poll. |
| `refreshes only once on open when the refreshed export still maps to nothing` | An already-expired server response cannot cause a refresh loop. |
| `server status replaces an empty cache after opening` | An empty cache cannot hide an export that already exists on the server. |
| `disables request and download while the status refreshes` | Actions cannot be tapped while the shared operation key would drop them. |
| `polls a running export after the polling interval` | RUNNING exports are re-checked every 10 seconds. |
| `does not poll before the polling interval elapses` | Polling never runs back-to-back. |
| `stops polling once the export is no longer running` | Polling ends when the status leaves RUNNING. |
| `stops polling a running export after thirty attempts` | A stuck export cannot poll forever; also guards against a refresh loop. |
| `shows the default screen when the server has no export` | NONE from the server renders the request screen. |
| `allows explicit refresh when a ready download becomes unavailable` | Recovery still refreshes even if the previous cached expiry was in the future. |
| `shows the default screen for a never requested export` | Cached NONE renders the request screen. |
| `renders a cached running export while its opening refresh is pending` | Preparing remains visible while the ViewModel refreshes; actions are disabled until loading ends. |
| `polls a later running observation after the polling interval` | A later RUNNING update starts polling and READY restores the download action. |
| `subscribes once and refreshes on open only for running records` | One database subscription; among cached rows, only RUNNING triggers the opening refresh. |
| `renders database changes without starting another refresh` | Local observations alone update the visible screen. |
| `refresh return values are not rendered before the database changes` | Refresh results cannot bypass the observer. |
| `start return values are not rendered before the database changes` | Start results cannot bypass the observer. |
| `failed opening refresh preserves cached data and offers retry` | A failed read retains the saved export and one inline error. |
| `database observation failures offer one inline retry` | Local read failures use resilient observation and one visible error. |
| `same cached status restores the default screen after rate limit recovery` | Re-saving NONE resets transient failure controls. |
| `ready status removes the loading refresh control after opening` | A ready observation replaces preparing and hides refresh. |
| `request presentation matches the approved document and passkey design` | Request uses its document icon and footer with no extra refresh control. |
| `preparing presentation separates the status message and footer` | Preparing keeps its return-later note below the refresh action. |
| `ready presentation shows the file with success and download icons` | Ready uses a file summary, success/download icons and a separate footer. |
| `missing cached status shows the request screen once the server has no export` | Null plus a NONE server status renders an enabled request action. |
| `shows preparing status without offering another request or download` | Running exports expose refresh only. |
| `shows ready download and backend expiry during the request cooldown` | Cooldown never blocks an existing ready file. |
| `does not fabricate missing expiry or cooldown timestamps` | Absent timestamps stay absent. |
| `null observation after expiry restores the default request screen` | Null restores the request screen without a synthetic status or an extra server call. |
| `ready export explains that a new request opens after it expires` | READY explains why Request is hidden, without the misleading one-day cooldown date. |
| `running export explains that a new request opens after it expires` | RUNNING shows the same expiry-based note even when the server reports a cooldown. |
| `blocks a new request during cooldown without hiding its availability date` | For NONE/FAILED the backend availability date explains the hidden action. |
| `offers a new request at the exact cooldown boundary` | Requests recover when the limit ends. |
| `refreshes running status into a ready download` | Refresh does not automatically download. |
| `shows an inline retry after a status failure` | Failed reads end loading and use one error presentation. |
| `clears the inline error after a successful retry` | Explicit refresh restores the request state. |
| `requests an export only after the user taps the action` | Starts use the existing confirmation operation. |
| `plays success haptic when an export request succeeds` | Haptic follows confirmed creation only. |
| `does not play success haptic when an export request fails` | Failure restores the action without success feedback. |
| `passkey cancellation leaves the request available without an error` | Cancellation stays silent. |
| `explains confirmation failures without automatically retrying` | Both confirmation errors require explicit recovery. |
| `rate limit rejection removes the request footer` | The whole status presentation switches to the blocked look, not just the buttons. |
| `rate limit rejection asks for status refresh before another request` | No repeated requests after a limit rejection. |
| `reuses downloaded JSON on subsequent download presses` | Two save/share actions use one backend download. |
| `failed downloads are requested again on retry` | Failed network reads cannot poison the file cache. |
| `new export timestamps discard the previously downloaded JSON` | A replacement export gets its own download. |
| `status and cooldown updates keep the download action available` | Local observations and cooldown changes leave download available; a successful refresh clears files. |
| `cleared or non ready status drops the downloaded file` | Clearing status or leaving READY releases the payload, including logout. |
| `shares the downloaded export file` | The returned file path reaches the `Share` navigation destination. |
| `unavailable download hides stale actions and offers status refresh` | No `Share` destination after the download becomes unavailable. |
| `unavailable download no longer presents the export as ready` | Title, icon, colour, footer and file row stop showing READY once the backend reports it unavailable. |
| `failed download can be retried without requesting another export` | A transport failure does not create another export. |
| `drops duplicate requests and lifecycle refreshes during confirmation` | Confirmation and status work cannot race. |
| `drops duplicate downloads while a download is active` | One download and share per active operation. |
| `drops repeated running observations while a status refresh is active` | Repeated RUNNING observations do not duplicate an active read. |
| `authentication failures use the shared notification handler` | Session handling is not replaced with an inline-only error. |
| `back action returns to Settings` | Back uses the shared navigation state. |

Updated `SettingsDashboardViewModelTest`: `sets the personal data export account label`
verifies the translated TextState label. The account row uses a standard SwiftUI NavigationLink and
the Android feature navigation action, matching notifications and passkeys; no ViewModel callback
or dashboard navigation destination is needed for this link.

The status flow is read-only and stays local, including for RUNNING records; expired records emit
null. `refresh()` always contacts the server, clears the export file when the response is not
downloadable, and caches its exact response. Domain tests cover expiry boundaries, unchanged
nonexpired emissions, file retention for downloadable responses, cleanup on non-downloadable
responses and failure propagation. ViewModel tests cover the opening
refresh, bounded RUNNING polling, loading state, duplicate refresh exclusion, inline retry and explicit
refresh actions.

Cache regressions in `CommonPersonalDataExportCacheTest`:

| Test | Protection |
|---|---|
| `missing cache emits null without using the network` | Unknown local data never causes a hidden network read. |
| `saved never requested status is distinct from a missing row` | NONE is persisted as a real record. |
| `restores status and all timestamps after datasource recreation` | Cache data survives datasource recreation. |
| `saving replaces the singleton status and notifies active observers` | A new snapshot replaces key 0 and reaches the screen. |
| `publishes an unchanged saved status so screen recovery can complete` | Equal cache writes remain observable for retry recovery. |
| `logout clears the saved status and notifies active observers` | The next account cannot inherit the previous status. |

Updated `GetPersonalDataExportImplTest` covers `observes a missing cache without fetching from the network`,
`observes a saved never requested status without fetching`, `refresh saves and returns the backend status`,
`failed refresh leaves the cached export intact` and `refresh propagates cache write failures`.
`StartPersonalDataExportImplTest` now verifies cache persistence on success and absence of cache writes on
failures; its new `start propagates cache write failures` and `cancelled confirmation never writes an export
status` cases cover the new write boundary and cancellation. Existing confirmation errors remain covered.

`PersonalDataExportMigrationTest` adds `adds an empty export cache while preserving existing user data`
and `stores a confirmed never requested status with nullable timestamps`. The generated version 25 schema
also compares equal to version 24 for every pre-existing entity; only the new export table is added.

### 5. Add owner-only business data export UI

Reuse the export interaction where practical, bind it to the selected business and restrict entry to
the owner. Handle business changes, lost access, confirmation, cooldown, expiry and save/share.

Review output: complete Android/iOS business-export flow and permission/state tests.

### 6. Add client-data retention to Business Settings

Display and edit the stored retention period, validate 12–120 months, explain its effect on ended
appointments and requests, and include the field in save/change detection.

Review output: Android/iOS setting, translations and boundary/save tests.

#### Step 6 review output

Implemented:

- Business Settings on Android and iOS displays the stored client-data retention period with a
  numeric keyboard, month units and an explanation of anonymizing or deleting client details on
  ended appointments and requests. Editing follows the existing business-update permission.
- Whole months from 12 through 120 are accepted. Empty, noninteger, overflowing and out-of-range
  input is rejected inline and cannot be submitted, including through a direct save callback.
  Correcting input restores the explanation and recalculates save availability.
- Retention participates in save and discard-confirmation state. Restoring the original value
  disables redundant saving; successful saves update the reference value and play a success haptic.
  Edits while saving cannot re-enable the button or start a second request.
- Backend error 200041 (`InvalidRetentionPeriod`) now marks the retention field invalid, preserves
  entered text and plays no success haptic. The existing generic-error regression retains invalid
  currency coverage; retention now has its own inline-recovery regression.
- The user requested a persisted default of 36 months and confirmed that the mobile database is
  authoritative for retention. `BusinessEntity` is now non-null with Kotlin and SQL defaults of 36;
  migration 22 → 23 fills missing periods and preserves custom values and dependent rows. Domain
  `Business` and `BusinessRemote` also use non-null `Int = 36`; mapping copies the value directly.
  `Business.Update` remains nullable to represent an omitted edit. Normal saves continue sending
  the persisted period. Business changes replace the displayed value and clear previous validation.
  The obsolete unknown-period UI branch and its strings have been removed.
- All new callbacks use weak ViewModel references. English, Russian, Ukrainian and Polish strings,
  the API audit and operation documentation are updated. The field is configured in `setup()`;
  the existing screen's InitData contract is unchanged.
- The current monolith Swagger contract was fetched on 2026-11-05. Existing remote/domain/cache
  support and typed errors were sufficient. The review refinement adds database version 23 and
  a retention-default migration; payloads, use-case logic, preferences, logout hooks and navigation
  are unchanged. Export UI remains deferred.

Validation:

- Red: the new ViewModel tests failed to compile before implementation because the retention
  screen-state field and resources did not exist.
- Green: all 234 Business tests pass: 91 presentation (55 Business Settings), 122 domain and 21 data.
  Sixteen retention regressions were added; existing permission tests now include the field.
- `:androidApp:assembleDevDebug` and `:shared:compileKotlinIosArm64` pass.
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme dev -configuration Debug
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build` passes.
- Spotless is applied to all six changed Kotlin files. `git diff --check` passes. All four translation
  files have matching keys, placeholders and newline markers.
- Backend access was limited to fetching Swagger; no live business update was submitted. Validation
  uses shared-state unit tests and builds, without adding UI-test tooling.

Added tests in `BusinessSettingsViewModelTest`:

| Test | Protection |
|---|---|
| `renders the stored retention period with its units and explanation` | Displays the exact stored value and guidance, with no initial unsaved change. |
| `renders the default retention period without unsaved changes` | The default 36-month period is displayed without enabling save. |
| `accepts both retention boundaries and enables saving` | Both 12 and 120 months are valid. |
| `rejects empty noninteger and out of range retention periods` | Empty, invalid, overflowing and values beyond either bound block saving. |
| `does not submit invalid retention even if save is invoked directly` | The save callback cannot bypass field validation. |
| `restores retention guidance after correcting invalid input` | Valid edits clear validation and restore save availability. |
| `disables saving when the original retention period is restored` | Reverting an edit removes the unsaved change. |
| `submits the selected retention period` | Save sends the edited number and establishes the saved reference. |
| `keeps default retention when saving unrelated changes` | Name-only edits keep the authoritative 36-month default. |
| `can change the default retention period` | An explicit edit replaces the 36-month default. |
| `asks before discarding a retention change` | Back navigation protects valid unsaved edits. |
| `asks before discarding an invalid retention change` | Back navigation also protects incomplete edits. |
| `shows retention rejection inline without a success haptic` | Server rejection keeps input, blocks saving and avoids success feedback. |
| `plays success haptic when retention is saved` | A confirmed retention save plays the existing success haptic. |
| `refreshes retention and clears validation when the business changes` | A new business cannot inherit the old period or error. |
| `does not enable save or submit twice while retention is being saved` | In-flight edits cannot cause duplicate saves; later edits remain unsaved. |

Review refinements:

- `renderBusiness` delegates to details, currency and socials helpers using per-field `.apply {}` blocks.
- Retention-default tests cover entity construction, null backfill, custom-value preservation,
  dependent-row preservation, future SQL insert defaults and null rejection. The former
  null-cache mapping regression now checks the non-null domain default and its cache round trip.
- All 240 database/Business tests pass after the default refinements (6 database tests plus the
  existing 234 Business tests). Android assembly, shared iOS compilation and compilation of the
  new migration tests for the iOS simulator pass. The migration matches the full exported version
  23 schema and foreign keys, with custom retention and dashboard selection preserved.

- The non-null domain refinement was red on `expected Int, actual Int?` before implementation.
  The two new `BusinessRetentionTest` regressions verify that the backend default period maps to 36
  and that a custom remote period is preserved. Default rendering and unrelated-save tests now use
  a non-null default business instead of constructing an impossible null domain value.

- The backend response refinement was red on the contract's non-null assertion before implementation
  (1 failure in 21 data tests). `BusinessRemote` now uses `Int = 36` with protobuf field 14 unchanged.
  Its contract regression checks nullability, the optional default and the pinned field number;
  the mapper fallback has been removed. Update-request retention remains nullable for omitted edits.

Step 6 awaits user review before another step starts.

### 7. Expose business-wide appointment requests

Status: Skipped for now at the user's request. Excluded from the current work sequence; revisit only if requested.

Connect the existing business-wide read use case to the request UI with appropriate access checks.
Keep the current employee's requests and business-wide requests distinct, including their refresh
behavior and caches.

Review output: Android/iOS request scope behavior and state tests.

### 8. Employee selection — excluded from mobile scope

Status: Not planned for the mobile app, per the user's product clarification.

The mobile app serves employees and business owners. Appointment creation is intentionally limited
to the employee attached to the current user. Preserve this behavior; no employee picker is needed.
Client-facing employee selection will be implemented on the website, outside this mobile plan.

### 9. Improve appointment error recovery

Status: Complete; the user authorized Step 10.

Implemented:

- The existing database observer reconciles selected services with refreshed options by id, removes
  missing selections and recalculates the subtotal and Create availability. Other form inputs stay
  intact. The next booking attempt is explicit and uses current service snapshots; no refresh return
  value is rendered alongside the observer.
- `ConcurrentModification` and every `EditWindowClosed` failure show a short error global message and
  reload the exact appointment. Completion closes its sheet first. Recovery does not retry mutations
  or play success haptics. Separate launch keys allow refresh from a mutation's error handler, while
  a shared mutation key prevents overlapping writes to the appointment.
- `GetAppointment.invoke(id)` still reads Room. Its new `refresh(id)` uses the verified
  `GET /api/appointments/{id}` route and then saves the returned aggregate through the existing cache
  write. Plain-text HTTP 404 becomes `Error.NotFound`; details close with an unavailable global
  message. Other refresh failures use the normal error handler and leave the previous cache intact.
- Completion, no-show and cancellation respect the seven-day window after scheduled end (start plus
  all booked service durations), inclusive at the cutoff. Rescheduling stays limited to scheduled
  appointments that have not started. Reloading recalculates all action visibility with an injected
  clock. The appended `UNRESOLVED` status decodes, caches and displays with no mutation actions.
- Android and iOS share the behavior through their existing state and notification handlers. English,
  Russian, Ukrainian and Polish messages, operation diagrams, API audit and database documentation
  are updated. No database migration, preference key, logout binding or navigation route is added.

Validation:

- Red: five service-reconciliation regressions failed on stale selections/subtotal. The status
  contract failed on the missing appended enum value. Refresh tests initially failed to compile
  against missing datasource/use-case methods. Seven recovery regressions failed before the new
  global messages and reloads; cancellation boundary tests required a domain predicate.
- Green: all 421 appointment tests pass: 41 data, 174 domain and 206 presentation.
- Android assembly (`:androidApp:assembleDevDebug`) and shared iOS compilation
  (`:shared:compileKotlinIosArm64`) pass.
- Changed Kotlin files are formatted, all seven affected Mermaid diagrams parse, and all four
  locales have matching string keys, placeholders and newline markers. `git diff --check` passes.
- Coverage includes service refresh/removal and preserved inputs, explicit booking retry, enum wire
  order and cache restoration, edit-window boundaries, GET route and protobuf response, plain-text
  404, suspended access, cache write/read failures, every recovery path, current details rendering,
  unavailable appointments, and absence of mutation retry or success haptics during recovery.
- Backend access was limited to fetching Swagger. HTTP behavior uses MockEngine; no live mutation
  was submitted. No UI-test tooling was added.

### 10. Align client-deletion UI with the current behavior

Apply the owner-only rule for deleting integrated clients. Explain appointment-data erasure and the
passkey confirmation before deletion. Keep detached-client deletion governed by its normal permission.

Review output: Android/iOS deletion behavior, translations and permission/state tests.


#### Step 10 review output

Status: Approved by the user.

Implemented:

- `GetClientsPermissions` now provides `canDeleteIntegrated`, comparing the target business's cached
  owner id with the first local profile emission. Missing business, owner or profile denies integrated
  deletion. Ownership of another business does not grant access. Ownership is independent of the
  delegated delete grant; detached clients continue using their normal delete permission.
- The Android and iOS edit screen uses the shared client-type-specific delete visibility. No platform
  state, navigation or DI registration changes are needed; existing constructor factories resolve the
  added authorization dependency inside clients domain implementation.
- The shared dialog explains irreversible erasure of client personal data from the business's
  appointments and the upcoming passkey confirmation. English, Russian, Ukrainian and Polish strings
  are updated together. The dialog callback uses a weak ViewModel reference.
- Cancel does not invoke deletion or navigate. Confirm continues through the existing action-bound
  passkey operation, remote DELETE and local cache invalidation. Success haptics and error behavior
  are preserved. No new endpoint, request model, database migration or persisted state is added.
- Operation diagrams/index, profile-observer consumers and the API audit are updated. Per the user's
  instruction, no server documentation fetch was required; the saved Step 9 API snapshot confirms the
  owner-only rule. No live deletion was performed.

Validation:

- Red: the initial tests failed to compile against the missing permission field and warning resource.
  A further regression failed because an absent cached profile still granted ownership when using
  `UserProfileCRUD.get()`. Switching to the local `observe().first()` lookup fixes that failure and
  avoids its remote fallback.
- Green: all 163 client tests pass: 15 data, 56 domain and 92 presentation. Twelve regressions were
  added; existing deletion confirmation, error, navigation and success/failure haptic tests still pass.
- `:androidApp:assembleDevDebug` and `:shared:compileKotlinIosArm64` pass.
- All four locales have matching string keys, placeholders and newline markers. All three affected
  operation diagrams parse with Mermaid. Changed Kotlin files pass Spotless; `git diff --check` passes.
- Both existing platform views and notification handlers were inspected; shared state tests verify
  their behavior without adding UI-test tooling. No Compose or SwiftUI source changes are required.

Added tests:

| Test file | Test | Protection |
|---|---|---|
| `GetClientsPermissionsImplTest` | `allows integrated client deletion for the business owner` | Ownership grants integrated deletion. |
| `GetClientsPermissionsImplTest` | `allows owner integrated deletion without a delegated delete grant` | Owner eligibility is distinct from detached-client permission. |
| `GetClientsPermissionsImplTest` | `denies integrated client deletion to employees with delete permission` | A delegated grant cannot bypass ownership. |
| `GetClientsPermissionsImplTest` | `ownership of another business does not allow integrated client deletion` | Ownership is scoped to the client's business. |
| `GetClientsPermissionsImplTest` | `missing cached owner denies integrated deletion while preserving detached deletion` | Unknown ownership does not remove ordinary grants. |
| `GetClientsPermissionsImplTest` | `missing cached user profile denies integrated deletion` | Permission lookup stays local and does not infer identity. |
| `EditClientViewModelTest` | `hides integrated client deletion for an employee with delete permission` | Employees cannot see an owner-only action. |
| `EditClientViewModelTest` | `shows integrated client deletion for the business owner` | Owners can see the action. |
| `EditClientViewModelTest` | `keeps detached client deletion governed by its normal permission` | Integrated eligibility cannot bypass detached-client permission. |
| `EditClientViewModelTest` | `warns about appointment data erasure and passkey verification` | The revised warning appears before any deletion operation. |
| `EditClientViewModelTest` | `cancelling the deletion dialog leaves the client unchanged` | Cancel performs no deletion, navigation or success feedback. |
| `EditClientViewModelTest` | `deletes the integrated client after the owner confirms` | Confirmation uses the correct client and returns to the client list. |
