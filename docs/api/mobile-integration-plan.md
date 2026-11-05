# Mobile API integration and UI plan

Status: Steps 1–3 reviewed and pushed. Step 6 is implemented and validated, awaiting user review. Steps 4–5 are deferred until the simpler steps are complete. Step 7 is skipped for now at the user's request. Step 8 is excluded from the mobile app's scope; Steps 9–10 have not started.

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

When the existing database observer delivers refreshed service options, reconcile selected services
with those options, recalculate the subtotal and require review before another booking attempt.
Keep the observer as the source of screen data; no additional refresh-result rendering is needed.

For `ConcurrentModification` and every `EditWindowClosed` failure, show a short global message and
reload the appointment. Do not automatically retry the rejected mutation. The user subsequently
requested local edit-window checks in the `Appointment.canBe…` methods: completion and no-show are
allowed through seven days after the scheduled end (start plus all booked service durations).
Rescheduling remains limited to scheduled appointments that have not started. Reloading applies
these checks to action visibility; server rejection handling remains necessary.

Review output: corrected state transitions and regression tests for stale selections and
message-and-reload recovery for both errors.

### 10. Align client-deletion UI with the current behavior

Apply the owner-only rule for deleting integrated clients. Explain appointment-data erasure and the
passkey confirmation before deletion. Keep detached-client deletion governed by its normal permission.

Review output: Android/iOS deletion behavior, translations and permission/state tests.
