# Mobile API integration and UI plan

Status: Step 1 implemented and validated; awaiting user review. Steps 2–10 have not started.

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
- Account deletion resolves the caller id, confirms the action with a nil business UUID, and sends the
  token-only `DeleteAccountRequestRemote`. The previous direct-assertion request model is removed.
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

The implementation is ready for evaluation. Do not begin Step 2 until the user has reviewed Step 1.

### 2. Add legal acceptance to signup

Load current legal versions and links, collect explicit acceptance, and submit the accepted version
identifiers. Handle documents changing during signup and avoid blocking signup when no documents
are published. Keep cross-feature access behind authorization-owned use cases.

Review output: Android/iOS signup flow, translations and ViewModel/use-case tests.

### 3. Add legal review and renewed acceptance in Settings

Replace Android's Google placeholder links and iOS's Contact Us destinations with current legal
document links. Show acceptance status and allow acceptance of newly effective versions.

Review output: Android/iOS Settings behavior and state tests.

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
