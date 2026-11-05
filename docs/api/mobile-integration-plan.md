# Mobile API integration and UI plan

Status: Steps 1–2 reviewed. Step 3 implemented and validated; awaiting user review. Steps 4–10 have not started.

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
Step 3 is ready for evaluation. Step 4 awaits user review of this step.

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

### 7. Expose business-wide appointment requests

Connect the existing business-wide read use case to the request UI with appropriate access checks.
Keep the current employee's requests and business-wide requests distinct, including their refresh
behavior and caches.

Review output: Android/iOS request scope behavior and state tests.

### 8. Add employee selection when creating appointments

Allow callers with appointment-update permission to select an eligible employee. Keep view-only
callers restricted to themselves and pass the selected employee id into the appointment draft.

Review output: Android/iOS picker and permission/selection tests.

### 9. Improve appointment error recovery

After quote/service changes, reconcile selected services with refreshed options, recalculate the
subtotal and require review before another booking attempt. Reload appointment details after a
concurrent-modification error. Hide or disable mutations after the seven-day edit window, with an
explanation, while retaining server-side rejection handling.

Review output: corrected state transitions and regression tests for stale selections, conflicts and
edit-window boundaries.

### 10. Align client-deletion UI with the current behavior

Apply the owner-only rule for deleting integrated clients. Explain appointment-data erasure and the
passkey confirmation before deletion. Keep detached-client deletion governed by its normal permission.

Review output: Android/iOS deletion behavior, translations and permission/state tests.
