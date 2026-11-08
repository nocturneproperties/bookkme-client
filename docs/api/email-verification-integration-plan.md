# Email verification and rate-limit integration plan

Status: Steps 1–2 committed; Step 3 implemented 2026-11-08, awaiting review. Steps 4–6 not started.

Source: `GET http://localhost/api/monolith/internal/swagger/documentation.yaml`, fetched 2026-11-08
(monolith built from `bookk-server` `e5f9d92a`, release notes `0.18.0`, issues #146 and #147).
Compared with the 2026-11-05 snapshot used by [the previous plan](mobile-integration-plan.md) and with
the current mobile data, domain and presentation code. Backend reference:
`~/IdeaProjects/bookk-server/docs/email-verification.md`.

> **Release blocker.** `POST /api/auth/sign_up` now requires `verificationId` and `emailCode` and
> answers 400 without them. The shipped app cannot create accounts against backend 0.18.0.
> Steps 2–3 must ship before, or together with, the backend deployment.

## Review checkpoints

Same rules as the previous plan: one numbered step at a time, each with a reviewable diff, validation
results and a short behaviour description; stop for review before the next step. New screens get
Android + iOS implementations and navigation; new strings get en/ru/uk/pl; strict TDD; operation and
database docs updated in the same change. HTTP behaviour is verified with `MockEngine`. In dev the
backend logs each email, code included, at INFO (`docker logs dev-monolith-1`), so a live end-to-end
sign-up is possible when the user asks for it.

## Confirmed API changes

| Area | Previous contract | Current contract | Mobile today |
|---|---|---|---|
| Sign-up challenge response | `RegistrationChallengeResponse` (fields 1–5) | `SignUpChallengeResponse`: same 1–5 plus `emailVerification` (6) = `EmailVerificationStarted { verificationId: Uuid (1), expiresAt: Instant (2), resendAvailableAt: Instant (3) }` | Decodes 1–5, ignores 6 |
| Sign-up challenge side effect | Passkey challenge only (5 min) | Also emails a 6-digit code (10 min) in the `Accept-Language` language; challenge now lives 10 min; calling again restarts with a new challenge **and** a new code | `Accept-Language` already sent (`NetworkDI.kt`) |
| Sign-up challenge errors | `EMAIL_EXIST` (1), `INVALID_EMAIL_FORMAT` (2) | + 422 `EMAIL_UNDELIVERABLE` (910005), 429 `EMAIL_CODE_RESEND_TOO_SOON` (910004) | New codes fall through as raw errors |
| `POST /api/auth/sign_up` body | `VerifyAccountCreationRequest` fields 1–5 | + `verificationId: Uuid` (6), `emailCode: String` (7), both required (400 if missing); `userInfo.email` is ignored | **Broken**: sends 1–5 only |
| `POST /api/auth/sign_up` errors | 3, 2, 5, 6, 4, 100003, 100004 | + `EMAIL_CODE_INVALID` (910001), `EMAIL_CODE_EXPIRED` (910002), `EMAIL_CODE_ATTEMPTS_EXCEEDED` (910003). The code is checked **before** the passkey; a wrong code does not spend the passkey challenge | Not mapped |
| Email change | `PATCH /api/user/me` with a new `email` | `POST /api/auth/email` `EmailChangeRequest { email (1) }` → `EmailVerificationStarted`; errors 1, 2, 910005, 429/910004. `POST /api/auth/email/confirm` `EmailConfirmation { verificationId (1), code (2) }` → 204; errors 910001–910003, `EMAIL_EXIST` (1, code stays usable) | Not integrated |
| `PATCH /api/user/me` | Changes email | Email may only be sent back unchanged or null; a different one is 422 `EMAIL_CHANGE_REQUIRES_VERIFICATION` (100006) | **Broken**: Edit Profile lets the email be edited and sends it |
| Client duplicates | `BUSINESS_CLIENT_EXISTS` (200004) for a taken phone **or** email | Only for a taken email; several clients may share a phone (create, update, import) | No client-side phone dedupe; create/edit have no typed error (server English message reaches the user) |
| Client linking | First booking could take over a client by phone | Only by email; profile email change merges clients by email | No mobile impact |
| Rate limits (nginx) | None | Per IP: `sign_in/challenge`, `sign_up/challenge` + `POST /api/auth/email`, `sign_up` + `email/confirm`. Beyond the burst nginx answers **429 with an HTML body** | 429 hits the protobuf error decoder (see Step 1) |
| Export rate-limit code | `CoreErrorCodes.DATA_EXPORT_TOO_FREQUENT` 900002 | `ExportErrorCodes.DATA_EXPORT_TOO_FREQUENT` 900002 (moved, same value) | No change |

Per-address code limits (backend, not nginx): 60 s resend cooldown, 5 sends an hour, 5 wrong codes per
verification (the sixth try ends it), only the newest code works.

No other path or shared schema changed. The internal `POST /api/internal/user/{id}/email` is backend-only.

## Work sequence

### 1. Rate-limit and error-body handling in `core/data`

`Throwable.toDomain()` (`core/data/.../map/Mapper.kt`) decodes every 400–500 body as
`BusinessServerError` protobuf. An nginx 429 has an HTML body; protobuf decoding of arbitrary bytes
can throw (→ `UnknownApiError(429)`) or, worse, succeed with a garbage `errorCode`.

- Decode the body as `BusinessServerError` only when `Content-Type` is `application/x-protobuf`.
- Add `Error.TooManyRequests` for a 429 without a protobuf business body. A protobuf 429
  (`EMAIL_CODE_RESEND_TOO_SOON`, `DATA_EXPORT_TOO_FREQUENT`) stays a `BusinessError`, so existing
  export handling is unchanged.
- Present `TooManyRequests` through the shared error mapper with a translated "Too many attempts. Try
  again in a moment." message, so sign-in gets it without screen changes.

Review output: mapper tests (HTML 429, protobuf 429, protobuf 4xx unchanged, non-protobuf 4xx keeps
today's fallbacks), error-mapper string test, en/ru/uk/pl string.

### 2. Sign-up email verification: data and domain

- `data`: rename the response model to `SignUpChallengeResponseRemote`, append
  `@ProtoNumber(6) emailVerification: EmailVerificationStartedRemote` (fields 1–3, `Uuid`/`Instant`,
  no defaults). Append `@ProtoNumber(6) verificationId: Uuid` and `@ProtoNumber(7) emailCode: String`
  to `VerifyAccountCreationRequest`, with no defaults. Extend `SignUpRemoteContractTest` with the new
  `(field, number)` pairs and an HTTP test proving the bytes on `POST /api/auth/sign_up`.
  `ServerSignUpChallenge` gains an `EmailVerification(id, expiresAt, resendAvailableAt)`;
  `RegistrationData` gains `verificationId` and `emailCode`.
- Add auth error codes 910001–910005 to `AuthErrorCodes` (one shared `EmailVerification` error family
  in `domain/api` so Step 5 reuses it).
- Split `CreateAccount` (one call: challenge → passkey → finish) into two authorization use cases:
  - `StartSignUp(userData): PendingSignUp`: requests the challenge (that sends the code) and returns
    the masked email, `expiresAt` and `resendAvailableAt`. Errors: `EmailAlreadyExist`,
    `InvalidEmailFormat`, `EmailUndeliverable`, `ResendTooSoon`. Calling it again is "resend".
  - `FinishSignUp(code)`: on the first call for a challenge, creates the passkey; every call then
    sends `finishRegistration` with the cached attestation and the code. Because the backend checks
    the code first and does not spend the challenge on a wrong code, `CodeInvalid` keeps the
    attestation so a retyped code needs **no second passkey prompt**. `CodeExpired`,
    `AttemptsExceeded` and `ChallengeWindowExpired` drop the pending sign-up and require a resend.
    On success: save tokens, invalidate client tokens, initial fetch, set authorized (unchanged tail).
- The pending sign-up (challenge JSON, request id, verification id, cached attestation) is in-memory
  only, in a `single` holder in authorization data, cleared on success, restart and logout. It is never
  persisted: it is a short-lived secret and process death simply restarts sign-up.
- A passkey created for a challenge that later expires is orphaned on the device. Signal it through
  the existing `PasskeyCredentialUpdater` best-effort cleanup when the pending sign-up is dropped, if
  the platform API allows signalling an unknown credential before an account exists. Otherwise
  document it as a known limitation.

Review output: contract/HTTP tests; `StartSignUpImplTest`, `FinishSignUpImplTest` covering happy path,
every error mapping, passkey created once across a wrong-code retry, a new passkey after resend,
cancellation silent, nothing saved on failure. Operation diagrams: replace `create-account.md` with
`start-sign-up.md` and `finish-sign-up.md`, plus README index rows.

### 3. Sign-up email verification: UI

- Sign-up screen: "Continue" now calls `StartSignUp` (legal acceptance unchanged) and navigates to a
  new **Verify email** screen (`screen/verifyemail`, parameterless state + `setup()`), built to be
  reused by Step 5:
  - "We sent a 6-digit code to m***@example.com", a 6-digit numeric field (`DIGIT`, `maxLength = 6`,
    one-time-code autofill: `textContentType(.oneTimeCode)` on iOS, autofill hint on Android). Submit
    automatically at 6 digits and with an explicit button.
  - Resend action, disabled with a countdown until `resendAvailableAt`. It is driven by an injected
    clock and tested with virtual time.
  - Inline errors on the code field: invalid (clears the field, keeps the attempt); expired/attempts
    exceeded (explains, offers resend); `ResendTooSoon` and `TooManyRequests` (try later).
    Undeliverable and taken email return to the sign-up form with the email field marked invalid.
  - Change-email back action returns to the form with data preserved.
  - Success: existing success haptic and navigation into the app.
- The sign-up form's existing legal rejection handling (Step 2 of the previous plan) moves with
  `FinishSignUp`: legal errors return to the form, reload documents and require renewed consent.
- Wiring checklist from AGENTS.md: state, VM, destinations, Android state/screen/nav/DI/factory, iOS
  accessor, Swift state/screen/factory, navigation on both platforms; en/ru/uk/pl strings.
- Decided during implementation: the verify screen tells the form about failures only the form can
  fix through shared domain state, not navigation results. A failure that drops the challenge keeps
  the user data in the in-memory session (`challenge = null`), so `ResendSignUpCode` can restart
  without the form. Taken, invalid or undeliverable email and legal rejections also store a
  `SignUpRejection`, which `SignUpViewModel` reads through `ObserveSignUpRejection` once its own start
  has succeeded. The verify screen receives the masked email and times as its `PendingSignUp`
  argument; Step 5 generalises the ViewModel to an email-change mode.

Review output: Android/iOS flow, `VerifyEmailViewModelTest` (sign-up mode) and updated
`SignUpViewModelTest`; builds on both platforms. Optional live dev sign-up using the logged code.

### 4. Edit Profile: stop changing the email through `PATCH /api/user/me`

- Make the Edit Profile email field read-only, showing the current address with a "Change" action
  (wired in Step 5; hidden until then if the steps ship separately).
- `EditProfile` sends the stored email unchanged. Map 422 100006 to a typed
  `EmailChangeRequiresVerification` defensively, presented inline on the email field.
- Remove email from the change detection and validation of the save button.

Review output: `EditProfileViewModelTest` and `EditProfileImplTest` updates; both platforms build.
Small and independent: can ship as a hotfix with Steps 2–3.

### 5. Change email with a verification code

- `data`: `POST /api/auth/email` and `POST /api/auth/email/confirm` routes, `EmailChangeRequestRemote`
  and `EmailConfirmationRemote` with contract tests, and `EmailVerificationStartedRemote` reused from
  Step 2. One method per call in an `EmailChangeDataSource` (authorization, which owns `/api/auth`).
- `domain`: `StartEmailChange(email): EmailVerification` (errors: taken, invalid format, undeliverable,
  resend too soon) and `ConfirmEmailChange(verificationId, code)` (errors: invalid, expired, attempts
  exceeded, taken). On success, re-fetch `/api/user/me` and upsert the Room profile so Settings, the
  dashboard and the ownership checks observe the new address. Settings uses authorization-owned wrapper
  use cases, as the layer rules require.
- `UI`: Edit Profile → "Change" → **Change email** (new address, validated with the existing
  `validateEmail`) → reused **Verify email** screen in email-change mode → back to Edit Profile with a
  success haptic and global message. `EMAIL_EXIST` at confirm is shown inline on the verify screen,
  and the code stays valid, so the user can go back and pick another address.

Review output: use-case tests (happy path, every error, profile refresh after confirm, no refresh on
failure), `ChangeEmailViewModelTest`, `VerifyEmailViewModelTest` (email-change mode), operation
diagrams `start-email-change.md` / `confirm-email-change.md`, updated `edit-profile.md`, strings.

### 6. Clients: duplicates are by email only

- Map `BUSINESS_CLIENT_EXISTS` (200004) on create/edit client to a typed `ClientEmailTaken` and
  present it inline on the email field ("A client with this email already exists"). Today the backend's
  English message reaches the user.
- Import result: rename `clients_import_result_reason_exists` text to say the email is already used
  (all four locales). Update the `ImportClientsImplTest` fixture message.
- Confirm that no client form or contact picker treats an equal phone as a duplicate (none found in this
  audit; keep it that way).

Review output: use-case and ViewModel tests, strings, `create-client`/`edit-client`/`import-clients`
diagrams if their error nodes change.

## Open decisions for review

1. **Verify email: separate screen (recommended) or a second step inside the sign-up screen.** A
   separate screen is reusable for the email change and keeps `SignUpViewModel` focused. The pending
   challenge lives in an in-memory holder rather than navigation arguments.
2. **When to create the passkey.** Recommended: after the code is entered (`FinishSignUp`). Users who
   never receive the email then leave no orphaned passkey, and wrong codes are retried without a
   second prompt. The alternative creates it right after the challenge, so the code is the last step.
3. **Steps 2–4 as one hotfix release.** Recommended, because the current app can neither sign up nor
   save a profile with a changed email against 0.18.0.
