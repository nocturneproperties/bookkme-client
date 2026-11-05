# Backend contract audit

Source: `GET http://localhost/api/monolith/internal/swagger/documentation.yaml`, fetched 2026-11-04.
Scope: 70 external API operations and 115 component schemas. Internal service-to-service, health,
metrics and JWKS routes are excluded from mobile coverage. This is a static contract audit, not a live
exercise of authenticated writes. No production code changed during this audit.

## Follow-up integration status

The findings below describe the pre-integration state. The following fixes have since been implemented
in data/domain layers with regression tests:

- Integrated-client patches omit protected personal fields.
- Polish device language is represented and mapped.
- Business retention is carried through remote/domain/cache models, with migration 21 → 22 and
  preservation when an update omits it. Business Settings now exposes the stored period on Android
  and iOS, validates whole months from 12 to 120, and reports backend error 200041 inline. Unknown
  periods in older cached rows become 36 months in migration 22 → 23; the non-null database column
  and domain model use the same default. The backend response model uses non-null retention too;
  remote-to-domain mapping copies it directly, preserving field number 14.
- Appointment edit-window and update validation/concurrency failures have typed domain errors.
- `GetAppointment.refresh(id)` uses the added `GET /api/appointments/{id}` endpoint and caches the
  returned aggregate. The existing `invoke(id)` remains local. Plain-text HTTP 404 maps to
  shared `core.domain.entity.Error.NotFound`, then to `GetAppointment.Error.NotFound`; 401 and
  suspended access keep the shared handling. Structured 404 business errors retain their code and
  message. Export downloads translate the shared `NotFound` to their existing `NotReady`. Details show
  short global messages and reload after concurrent modification or a closed edit window without
  retrying the mutation. Missing or inaccessible refreshed appointments close details.
- The current appointment schema appends `UNRESOLVED` at enum ordinal 4. Mobile remote/domain
  enums, mappings, text-based cache restoration and localized shared UI status support it while
  retaining ordinals 0–3. Unresolved appointments show no mutation actions. No database migration
  or change to appointment protobuf field numbers is required.
- Service validation, business update currency/retention validation, profile contact conflicts and
  last-passkey deletion have typed errors; the service-group error code is corrected to 200012.
- Existing accounts can submit legal-document acceptance through a registered use case.
- A registered business-wide appointment-request read is available without sharing the `/mine` cache.

The three product decisions have now been integrated:

- Instant creation fetches a fresh quote, validates the displayed service data, refreshes the service
  cache on mismatch, and throws without silently rebooking. It sends the backend's ID-only draft.
- Client deletion performs passkey confirmation internally and evicts referencing cached appointment
  and request aggregates after a successful DELETE, within the client-removal transaction. Android and
  iOS now show integrated-client deletion only to the selected client's business owner; detached-client
  deletion keeps its ordinary permission. The shared dialog explains irreversible appointment-data
  erasure and the upcoming passkey confirmation.
- Personal and business export start/status/download operations are registered. Starts perform
  action-bound confirmation; downloads return raw JSON strings without creating local files.

All nine previously missing external operations now have domain/data entry points. The findings below
are retained as the original audit evidence; they no longer describe missing implementations. Existing
ViewModels now show specific mutation/validation errors. Signup now exposes current legal documents and
explicit version-bound acceptance on Android and iOS. Settings now exposes published document links,
current acceptance status and explicit renewed acceptance on both platforms. Exports and business-wide
request entry points still need UI work to expose them.


## ActionConfirmation follow-up

The current schema was fetched again for Step 1 of the [mobile integration plan](mobile-integration-plan.md).
The obsolete `POST /api/auth/passkey/step_up` operation is replaced by
`POST /api/auth/passkey/confirm_action`, with `ActionConfirmationRequest` and `ActionConfirmationToken`.
The implementation now uses that route and model names. Existing confirmation request wire tags and
existing action enum ordinals are preserved; `DELETE_ACCOUNT` is appended.

Client deletion and personal/business export requests now name their field `confirmationToken`, retaining
wire tag 1. The corresponding error constants are renamed to `BUSINESS_ACTION_CONFIRMATION_INVALID`
(200040) and `USER_ACTION_CONFIRMATION_INVALID` (100005).

Account deletion now composes the shared confirmation operation with `DELETE_ACCOUNT`, a nil business
UUID and the caller's user UUID as target. The old assertion-shaped `VerifyDeleteAccRequest` has been
removed from the backend. Its replacement is a separate `DeleteAccountRequest` message containing only
`confirmationToken` at tag 1, represented by `DeleteAccountRequestRemote` on mobile. Errors 12
(`ACTION_CONFIRMATION_INVALID`) and 4 (`VERIFICATION_FAILED`) map to the existing account-verification
failure. Challenge and ownership failures are handled during shared confirmation. User cancellation and
unrelated failures propagate without performing deletion or logout/cache cleanup.

The confirmation result retains the verified assertion in memory so account deletion can still signal
credential removal after the remote DELETE succeeds, without a second prompt. Signaling remains
best-effort; logout and token/status cleanup follow successful deletion. No confirmation token or assertion
is persisted, and no database schema, preference key or logout-hook binding changes are required.

`NotificationSettings.Update` was renamed to `NotificationSettingsUpdate` in the schema, with unchanged
properties; the existing mobile notification update payload remains compatible.

The original findings below retain their historical route/model names. Validation uses mocked HTTP
requests and domain tests; no live destructive operation has been performed.

## Settings legal review follow-up

The schema was fetched again on 2026-11-05 for Step 3. `GET /api/user/me/legal` still returns each
currently effective document and `acceptedAt` (absent when pending). `POST /api/user/me/legal` accepts
explicit version identifiers, leaves omitted documents unchanged and rejects unpublished versions with
100003. No contract changes were required.

Both Settings implementations now use these existing operations. Link taps only open the published URL;
the explicit acceptance action sends pending displayed versions and then reloads status. Unknown-version
rejection reloads documents for another review. Loading failures offer retry and block acceptance.
Validation uses shared state tests and builds; no live authenticated acceptance was submitted.

## Existing calls with incompatible contracts

### P1 — Instant appointment creation sends the wrong request type

Backend: `POST /api/appointments/instant` accepts `InstantAppointmentDraft` with `businessId`,
`employeeId`, `services: RequestedService[]`, `date`, `note`, `offerToken` and `clientId`.

App: [CommonAppointmentDataSource](../../feature/appointments/data/src/commonMain/kotlin/me/bookk/feature/appointments/data/datasource/CommonAppointmentDataSource.kt)
sends `appointment.toRemote()`, which produces the response-shaped `AppointmentRemote`. Its fields
include appointment id, initiator id, employee/client snapshots and service snapshots, instead of the
required draft. This is a protobuf payload mismatch, not merely a different Kotlin class name.
[AppointmentDraft](../../feature/appointments/domain/api/src/commonMain/kotlin/me/bookk/feature/appointments/domain/api/entity/AppointmentDraft.kt)
has no offer token or independently selected employee id. `CreateAppointmentImpl` synthesizes an
appointment and always assigns the current employee.

Required work: carry the selected employee, quoted offer token and service counts through the domain;
introduce the actual draft request; map all documented creation errors. Quote retrieval, if orchestrated
by this operation, belongs in the use case, not a combined datasource method.

Missing regression: an HTTP request test decoding the outgoing instant-creation body against the
backend draft contract. Existing appointment descriptor tests validate response models, which cannot
prove that a write endpoint receives the right request type.

### P1 — Client deletion lacks passkey confirmation and its request body

Backend: `DELETE /api/business/{businessId}/clients/{id}` accepts `DeleteClientRequest.stepUpToken`.
A missing/invalid token can return `BUSINESS_STEP_UP_INVALID` (200040). The token is issued by
`POST /api/auth/passkey/step_up`, bound to `DELETE_CLIENT`, the business and target client, and valid
for two minutes and one use.

App: [CommonClientsDataSource.deleteClient](../../feature/clients/data/src/commonMain/kotlin/me/bookk/feature/clients/data/datasource/CommonClientsDataSource.kt)
sends a bodyless DELETE. Neither the datasource interface nor `DeleteClient` accepts or obtains a token;
there is no step-up route/model/use case. Local cache deletion runs only after successful remote deletion,
so a rejected request does not incorrectly remove the cached client.

### P1 — Integrated-client note edits still send protected personal fields

Backend: PATCH clients permits only `description` changes for integrated clients; personal fields are
updated when non-null and can trigger `BUSINESS_CLIENT_PERSONAL_INFO_NOT_EDITABLE` (200026).

App: [Client.toUpdateRemote](../../feature/clients/data/src/commonMain/kotlin/me/bookk/feature/clients/data/mapping/ClientMapper.kt)
always copies name, last name, phone and email into the patch, even for `Client.Integrated`.
The presentation layer preserves these values when editing notes, but does not remove them from the
wire request. The mapper must omit those fields for integrated clients. The domain currently has no
specific mapping for 200026.

### P1 — Polish notification-device responses cannot be decoded

Backend: `Device.language` enum is `EN`, `UK`, `PL`.

App: [DeviceLanguageRemote](../../feature/settings/data/src/commonMain/kotlin/me/bookk/feature/settings/data/remote/model/DeviceRemote.kt)
and [DeviceLanguage](../../feature/settings/domain/api/src/commonMain/kotlin/me/bookk/feature/settings/domain/api/entity/Device.kt)
only contain `EN`, `UK`. `PUT /api/notifications/{deviceUuid}/token` decodes the returned device, so a
Polish response can fail after the token was accepted remotely. `UpdateNotificationTokenImpl` then
records it as pending and retries later. Missing regression: decode a backend PL value and map it to
the domain enum.

## Missing fields and incomplete error handling

### P2 — Business client-data retention (implemented through the UI)

Backend: `Business.clientDataRetentionMonths` and nullable
`BusinessUpdateModel.clientDataRetentionMonths`; allowed range 12–120 months, error 200041.

Resolved: remote/domain/cache models carry the period. Room migration 21 → 22 added the field;
22 → 23 makes it non-null and replaces missing periods with 36 months while retaining custom values.
Both `BusinessRemote` and domain `Business` use non-null retention with a default of 36.
Remote-to-domain mapping copies the value directly. Business Settings on Android and iOS displays the stored value and
explains retention
of client details on ended appointments and requests, validates 12–120 whole months, and includes it
in save and unsaved-change detection. Backend error 200041 marks the field invalid with a recovery
message. The mobile database is authoritative for this setting and stores 36 months when the value
is missing. Normal settings saves continue sending that persisted value. The Business API contract
was rechecked against the monolith Swagger endpoint on 2026-11-05. The non-null domain refinement
requires no wire-tag changes or further database migration.

### P2 — New appointment mutation errors have no dedicated domain mapping

`APPOINTMENT_EDIT_WINDOW_CLOSED` (300026) applies to update, cancellation, completion and no-show;
the backend rejects changes after the appointment ended more than seven days ago. The constant and
operation-specific errors are absent.

`UpdateAppointmentImpl` additionally does not map `DATE_IN_PAST` (300012),
`SERVICE_SELECTION_INVALID` (300024), `BUSINESS_QUOTE_SERVICE_NOT_FOUND` (200013),
`BUSINESS_EMPLOYEE_NOT_EXISTS` (200024), or `TRANSACTION_CONFLICT` (900001 / HTTP 409).
The latter currently has no retry/reload behavior. Instant creation maps only overlap/date/time errors,
missing the documented quote/token/duration/service/employee and past-date failures.

These failures propagate as generic business errors; this is a domain-handling gap, not silent success.
Affected files: [UpdateAppointmentImpl](../../feature/appointments/domain/impl/src/commonMain/kotlin/me/bookk/feature/appointments/domain/impl/UpdateAppointmentImpl.kt),
[CreateAppointmentImpl](../../feature/appointments/domain/impl/src/commonMain/kotlin/me/bookk/feature/appointments/domain/impl/CreateAppointmentImpl.kt),
`CancelAppointmentImpl`, `CompleteAppointmentImpl`, `MarkAppointmentNoShowImpl`.

### P2 — Other documented errors remain generic

- Business create/update: unsupported currency 200035; update currency locked 200036 and retention
  out of range 200041. `UpdateBusinessImpl` maps only schedule errors.
- Service create/edit: price precision 200037, invalid business currency 200038, missing service group
  200012; existing name/duplicate errors also have no typed domain mapping in these use cases.
- Profile edit: contact already taken 100002.
- Passkey deletion: last remaining passkey 9; passkey addition challenge generation failure 10.
- Account deletion: expired challenge 6, missing passkey owner 7 and passkey owned by another account
  11. Only verification failed 4 is mapped explicitly.

The shared HTTP mapper preserves server error codes/messages as `Error.BusinessError`, so these
cases still report a failure. Dedicated errors are needed where callers should recover or display a
specific state. The published schema also lists error 9 on passkey-add finish; that unusual backend
contract should be clarified rather than assigned a guessed meaning.

### P3 — Incorrect unused service error constant

[ServiceErrorCodes](../../feature/services/data/source/src/commonMain/kotlin/me/bookk/feature/services/domain/datasource/ServiceErrorCodes.kt)
defines `BUSINESS_SERVICE_GROUP_NOT_EXISTS = 200010`; the schema specifies 200012. It is currently
unused, so it does not independently cause a runtime branch failure, but must be corrected when adding
service error mapping.

## External operations with no mobile implementation

Nine operations have no datasource/use-case path:

| Operation | Missing capability |
|---|---|
| `POST /api/auth/passkey/step_up` | Passkey confirmation token for a specific sensitive action and target |
| `POST /api/user/me/legal` | Accept a newly published document version for an existing account; legal reads and signup acceptance are already implemented |
| `POST /api/user/export` | Start personal data export with confirmation; handle 100005 and rate limit 900002 |
| `GET /api/user/export` | Read export status and expiry/cooldown timestamps |
| `GET /api/user/export/download` | Download personal export JSON |
| `POST /api/business/{id}/export` | Start owner-only business export with confirmation; handle 200040 and rate limit 900002 |
| `GET /api/business/{id}/export` | Read business export status |
| `GET /api/business/{id}/export/download` | Download business export JSON |
| `GET /api/appointments/request/{businessId}` | Fetch business-wide requests; only the caller's `/mine` requests are fetched today |

Internal booking-context and internal export routes are intentionally not listed as missing mobile APIs.

## Cache consequence to address with deletion

The delete-client route now documents erasure from the business's appointments. `DeleteClientImpl`
only removes the local client row. Appointment/request client snapshots are denormalized and have no
foreign key to the client; their saved personal details can remain until refreshed or cleared. When
integrating confirmed deletion, define a targeted snapshot invalidation/redaction strategy so cached
appointment details match the successful backend erasure. This consequence is inferred from the route
contract and local write paths; no live deletion was performed.

## Verification limits

The schema identifies names, types, required fields, enum order, errors and routes. It does not expose
explicit protobuf field-number extensions for every model. Existing sparse numbers (for example
appointment update fields 1, 8, 9, 13, 14) must not be renumbered merely to follow the displayed property
order. Confirm backend wire tags before changing existing numbers.

No production code or UI was changed and no authenticated write was executed. Existing passing tests
are not proof of endpoint compatibility when a request is mocked without checking its serialized body.
