# Operation activity diagrams

One Mermaid flowchart per domain use case (`feature/<name>/domain/api`, implemented in `domain/impl`),
grouped by feature. The mobile client has no routes of its own. A use case is the unit that turns a UI action
into network calls and local database or DataStore writes, so each diagram shows those
steps. Its nodes name the actual datasource methods, HTTP routes, backend error codes and domain `Error`
types.

Unlike bookk-server's operation docs, **reads are included**. On mobile, a read is rarely a plain GET: most
lists are a `flow()` observing Room plus a `refresh()` that fetches, deletes stale rows and upserts. The
diagrams show exactly which table a screen observes and what rewrites it. For the tables themselves, see
[Local database ER diagrams](../database/README.md). For the server-side behaviour behind each route, see
bookk-server `docs/operations/`.

**Business access suspended.** Every business-scoped route in the `business` and `appointments` services
answers `403` with `BUSINESS_EMPLOYEE_ACCESS_SUSPENDED` (200034) when the caller is a suspended employee of that
business. The diagrams do not repeat this branch. `Throwable.toDomain()` (`core/data/.../map/Mapper.kt`) turns
that response into `Error.BusinessAccessSuspended` instead of `Error.BusinessError`, so `onBusinessError`
blocks rethrow it untouched. `ErrorMapperImpl` maps it to `PresentationNotification.BusinessAccessSuspended`,
which the platform notification observers hand to a root-level handler (`LocalBusinessAccessSuspendedHandler`
on Android, the `BusinessAccessSuspendedHandler` environment object on iOS), the same way `Unauthorized` reaches
`BootstrapViewModel.logOut()`. The handler calls `BootstrapViewModel.onBusinessAccessSuspended()`, which shows the
"Access suspended" message and runs [Initiate business suspend](authorization/initiate-business-suspend.md), which
clears the dashboard selection and reloads every business, so the dashboard falls back to its Get started screen
where the user can pick one of the remaining businesses. The call is keyed with `LaunchBehaviour.DropLatest`, so suspensions reported
while a refresh is running are dropped.

Legend: `[(…)]` is a Room or DataStore access, `[…]` with an HTTP verb is a network call, and `([…])` is a
terminal (return, emit or throw). "local only" means the use case never touches the network.

**Not found.** HTTP 404 responses without a structured business-error body become the shared
`me.bookk.core.domain.entity.Error.NotFound`, retaining the original cause. Use cases translate that
type into their own errors where needed (appointment `NotFound`, export-download `NotReady`). A 404
carrying a structured business-error body retains its `Error.BusinessError` code and message.

- [Authorization](#authorization)
- [Business](#business)
- [Clients](#clients)
- [Services](#services)
- [Employees](#employees)
- [Appointments](#appointments)
- [Settings](#settings)
- [Use cases composed of other use cases](#use-cases-composed-of-other-use-cases)
- [Known issues found while documenting](#known-issues-found-while-documenting)

## Authorization

| Use case | Network | Diagram |
|---|---|---|
| `ConfirmSensitiveAction` | `GET challenge + POST /api/auth/passkey/confirm_action` | [Confirm sensitive action](authorization/confirm-sensitive-action.md) |
| `GetSignUpLegalDocuments` | `invoke`: persisted cache only; `refresh`: via `GetLegalDocumentVersions`, `GET /api/user/legal` | [Get signup legal documents](authorization/get-sign-up-legal-documents.md) |
| `StartSignUp` | `POST /api/auth/passkey/sign_up/challenge` (also emails the 6-digit code) | [Start sign-up](authorization/start-sign-up.md) |
| `FinishSignUp` | `POST /api/auth/sign_up` with the emailed code and accepted legal-document versions | [Finish sign-up](authorization/finish-sign-up.md) |
| `ResendSignUpCode` | via `StartSignUp`, `POST /api/auth/passkey/sign_up/challenge` with the user data kept in the sign-up session | [Resend sign-up code](authorization/resend-sign-up-code.md) |
| `ObserveSignUpRejection` | local only: in-memory sign-up session | [Observe sign-up rejection](authorization/observe-sign-up-rejection.md) |
| `SignIn` | `GET /api/auth/passkey/sign_in/challenge`, `POST /api/auth/sign_in` | [Sign in](authorization/sign-in.md) |
| `InitiateBusinessSuspend` | via `SwitchDashboardBusiness(null)` (local only) and `RefreshBusinessInfo` (`GET /api/business/mine`) | [Initiate business suspend](authorization/initiate-business-suspend.md) |
| `InitialAppDataFetch` (+ internal `LowPriorityDataFetch`) | `timestampProtectedFetch`: local expired personal and business export file cleanup, then `GET /api/user/legal`; private requests only when authorized; `rawFetch`: profile, business, per-feature refreshes | [Initial app data fetch](authorization/initial-app-data-fetch.md) |
| `LogOut` | `DELETE /api/auth/session`; bound cleanup includes export status and temporary files | [Log out](authorization/log-out.md) |
| `RefreshToken` | `POST /api/auth/refresh` | [Refresh token](authorization/refresh-token.md) |
| `UserProfileCRUD` | `GET` / `PATCH /api/user/me` | [User profile CRUD](authorization/user-profile-crud.md) |
| `IsUserLoggedIn`, `GetTokenInfo`, `GetSettingsColorScheme` | local only | [Is user logged in / Get token info / Get settings color scheme](authorization/is-user-logged-in.md) |

## Business

| Use case | Network | Diagram |
|---|---|---|
| `DeleteExpiredBusinessDataExportFiles` | Local only, on every app start from `InitialAppDataFetch.timestampProtectedFetch`: every cached `business_data_export` row → delete `business_data_exports/<businessId>/` unless READY and unexpired | [Delete expired business data export files](business/delete-expired-business-data-export-files.md) |
| `DownloadBusinessDataExport` | Local READY status of the business (else `NotReady`) → reuse JSON file per business and export; cache miss → `GET /api/business/{id}/export/download` → save through `FileProvider` → return file path for the `Share` destination; `NotFound` → `NotReady` | [Download business data export](business/download-business-data-export.md) |
| `GetBusinessDataExport` | Read-only per-business flow maps expired records to null; ViewModel refreshes on open only for a null or RUNNING first value and polls RUNNING every 10 s (max 30); `refresh` uses `GET /api/business/{id}/export` → clear that business's files unless READY and unexpired → save server response; 404 → forget row and file → `NotOwner` | [Get business data export](business/get-business-data-export.md) |
| `StartBusinessDataExport` | `POST /api/business/{id}/export`; passkey confirmation → clear that business's JSON file → status cache upsert; 404 → forget row and file → `NotOwner` | [Start business data export](business/start-business-data-export.md) |
| `CreateBusiness` | `POST /api/business`; typed backend errors | [Create business](business/create-business.md) |
| `UpdateBusiness` | `PUT /api/business/{id}`; Business Settings retention (12–120 months; cache default 36) + inline validation errors | [Update business](business/update-business.md) |
| `RefreshBusinessInfo` | `GET /api/business/mine`; cache retention defaults to 36 months | [Refresh business info](business/refresh-business-info.md) |
| `SwitchDashboardBusiness` | `PUT /api/business/{id}/dashboard` (best-effort) | [Switch dashboard business](business/switch-dashboard-business.md) |
| `JoinBusiness` | via `RedeemEmployeeInvitation` | [Join business](business/join-business.md) |
| `EnableAppointmentsPlugin` | `POST /api/appointments/enabled/{businessId}` | [Enable appointments plugin](business/enable-appointments-plugin.md) |
| `IsAppointmentsPluginEnabled` | `GET /api/appointments/enabled/{businessId}` | [Is appointments plugin enabled](business/is-appointments-plugin-enabled.md) |
| `CanEditBusiness` | local only (`business` table) | [Can edit business](business/can-edit-business.md) |
| `GetAvailableDashboardFeatures` | local only; `DATA_EXPORT` only for the owner (`business.ownerId` = cached profile id) | [Get available dashboard features](business/get-available-dashboard-features.md) |
| `ObserveDashboardSetupStatus` | local only | [Observe dashboard setup status](business/observe-dashboard-setup-status.md) |
| `RefreshDashboardSetupStatus` | via `IsAppointmentsPluginEnabled.refresh`, `GetServiceGroups.refresh`, `GetServices.refresh`, `GetClientsList.refresh` | [Refresh dashboard setup status](business/refresh-dashboard-setup-status.md) |
| `ObserveDashboardBusinessChanges`, `ObserveDashboardBusinessIdChanges`, `ObserveCurrentBusinessId` (×4), `ObserveCurrentBusiness` | local only | [Observe dashboard business](business/observe-dashboard-business-changes.md) |
| `ObserveUserBusinessesChanges` | local only | [Observe user businesses](business/observe-user-businesses-changes.md) |

## Clients

| Use case | Network | Diagram |
|---|---|---|
| `GetClientsList` | `GET /api/business/{businessId}/clients` | [Get clients list](clients/get-clients-list.md) (reference list steps) |
| `GetClient` | local only (`flow` observes the row) | [Get client](clients/get-client.md) |
| `CreateClient` | `POST /api/business/{businessId}/clients` | [Create client](clients/create-client.md) |
| `ImportClients` | `POST /api/business/{businessId}/clients/import` (one request per 500 entries) | [Import clients](clients/import-clients.md) |
| `GetDeviceContacts` | local only (device address book) | [Get device contacts](clients/get-device-contacts.md) |
| `EditClient` | `PATCH /api/business/{businessId}/clients/{id}`; integrated-client patch | [Edit client](clients/edit-client.md) |
| `DeleteClient` | passkey confirmation + `DELETE /api/business/{businessId}/clients/{id}` | [Delete client](clients/delete-client.md) |
| `GetClientsPermissions` | local only (`business` and `user_profile` observers; owner-only integrated deletion) | [Get clients permissions](clients/get-clients-permissions.md) |

## Services

| Use case | Network | Diagram |
|---|---|---|
| `GetServices` | `GET /api/business/{businessId}/service` | [Get services](services/get-services.md) |
| `GetService` | local only | [Get service](services/get-service.md) |
| `CreateService` | `POST /api/business/{businessId}/service`; typed validation errors | [Create service](services/create-service.md) |
| `EditService` | `PUT /api/business/{businessId}/service/{id}`; typed validation errors | [Edit service](services/edit-service.md) |
| `DeleteService` | `DELETE /api/business/{businessId}/service/{id}` | [Delete service](services/delete-service.md) |
| `GetServiceGroups` | `GET /api/business/{businessId}/service_group` | [Get service groups](services/get-service-groups.md) |
| `CreateServiceGroup` | `POST /api/business/{businessId}/service_group` | [Create service group](services/create-service-group.md) |
| `DeleteServiceGroup` | `DELETE /api/business/{businessId}/service_group/{id}` | [Delete service group](services/delete-service-group.md) |
| `CreateQuote` | `POST /api/business/{businessId}/service/quote` | [Create quote](services/create-quote.md) |
| `GetBusinessCurrency` | local only | [Get business currency](services/get-business-currency.md) |
| `GetServicesPermissions` | local only (`business` table via `ObserveUserBusinessesChanges`) | [Get services permissions](services/get-services-permissions.md) |

## Employees

| Use case | Network | Diagram |
|---|---|---|
| `GetEmployees` | `GET /api/business/{businessId}/employee` | [Get employees](employees/get-employees.md) |
| `GetEmployee` | local only (`employee` tables) | [Get employee](employees/get-employee.md) |
| `UpdateEmployee` | `PUT /api/business/{businessId}/employee/{id}` + `PUT /api/business/{businessId}/employee/{id}/permissions` (parallel; permissions skipped for the owner) | [Update employee](employees/update-employee.md) |
| `SetEmployeeSuspension` | `PUT /api/business/{businessId}/employee/{id}/suspension` | [Set employee suspension](employees/set-employee-suspension.md) |
| `GetAssignableServices` | via `GetServices` | [Get assignable services](employees/get-assignable-services.md) |
| `IsBusinessOwner` | local only (`business` table via `ObserveUserBusinessesChanges`; current-user overload also `UserProfileCRUD`) | [Is business owner](employees/is-business-owner.md) |
| `CanEditEmployees` | local only (`business` table via `ObserveUserBusinessesChanges`) | [Can edit employees](employees/can-edit-employees.md) |
| `GetEmployeeInvitations` | `GET /api/business/{businessId}/employee_invitation` | [Get employee invitations](employees/get-employee-invitations.md) |
| `CreateEmployeeInvitation` | `POST /api/business/{businessId}/employee_invitation` | [Create employee invitation](employees/create-employee-invitation.md) |
| `RevokeEmployeeInvitation` | `POST /api/business/{businessId}/employee_invitation/{id}/revoke` | [Revoke employee invitation](employees/revoke-employee-invitation.md) |
| `RedeemEmployeeInvitation` | `POST /api/business/employee_invitation/redeem` | [Redeem employee invitation](employees/redeem-employee-invitation.md) |

## Appointments

| Use case | Network | Diagram |
|---|---|---|
| `GetBusinessAppointmentRequests` | `GET /api/appointments/request/{businessId}` | [Get business appointment requests](appointments/get-business-appointment-requests.md) |
| `GetBusinessAppointmentsEnabled` | `GET /api/appointments/enabled` | [Get business appointments enabled](appointments/get-business-appointments-enabled.md) |
| `GetAppointmentsForBusiness` | `GET /api/appointments/list/{businessId}?date=[&employeeId=]` | [Get appointments for business](appointments/get-appointments-for-business.md) |
| `GetAppointment` | local read; `refresh(id)` fetches and caches `GET /api/appointments/{id}`; shared `NotFound` → use-case `NotFound` | [Get appointment](appointments/get-appointment.md) |
| `GetAppointmentHistory` | `GET /api/appointments/history/{businessId}` (paged) | [Get appointment history](appointments/get-appointment-history.md) |
| `CreateAppointment` | fresh service quote + `POST /api/appointments/instant`; refresh on mismatch; UI reconciles selected services from cache | [Create appointment](appointments/create-appointment.md) |
| `UpdateAppointment` | `PUT /api/appointments/{id}`; UI reloads after conflict or closed edit window | [Update appointment](appointments/update-appointment.md) |
| `CancelAppointment` | `POST /api/appointments/{id}/cancel`; UI reloads after closed edit window | [Cancel appointment](appointments/cancel-appointment.md) |
| `CompleteAppointment` | `POST /api/appointments/{id}/complete`; UI reloads after closed edit window | [Complete appointment](appointments/complete-appointment.md) |
| `MarkAppointmentNoShow` | `POST /api/appointments/{id}/no-show`; UI reloads after closed edit window | [Mark appointment as no-show](appointments/mark-appointment-no-show.md) |
| `GetOwnAppointmentRequests` | `GET /api/appointments/request/{businessId}/mine` | [Get own appointment requests](appointments/get-own-appointment-requests.md) |
| `CreateAppointmentRequest` | `POST /api/appointments/request` | [Create appointment request](appointments/create-appointment-request.md) |
| `ApproveAppointmentRequest` | `POST /api/appointments` | [Approve appointment request](appointments/approve-appointment-request.md) |
| `DeclineAppointmentRequest` | `POST /api/appointments/request/{id}/decline` | [Decline appointment request](appointments/decline-appointment-request.md) |
| `GetAppointmentSettings` | `GET /api/appointments/settings/{businessId}` | [Get appointment settings](appointments/get-appointment-settings.md) |
| `UpdateAppointmentSettings` | `PUT /api/appointments/settings/{businessId}` | [Update appointment settings](appointments/update-appointment-settings.md) |
| `GetAppointmentOptions` | composes two refreshes | [Get appointment options](appointments/get-appointment-options.md) |
| `ObserveAppointmentOptions` | local only | [Observe appointment options](appointments/observe-appointment-options.md) |

## Settings

| Use case | Network | Diagram |
|---|---|---|
| `DeleteExpiredPersonalDataExportFile` | Local only, on every app start from `InitialAppDataFetch.timestampProtectedFetch`: cached `personal_data_export` row → delete `personal_data_exports/` unless READY and unexpired | [Delete expired personal data export file](settings/delete-expired-personal-data-export-file.md) |
| `DownloadPersonalDataExport` | Local READY status (else `NotReady`) → reuse JSON file per export; cache miss → `GET /api/user/export/download` → save through `FileProvider` → return file path for the `Share` destination; `NotFound` → `NotReady` | [Download personal data export](settings/download-personal-data-export.md) |
| `GetPersonalDataExport` | Read-only flow maps expired records to null; ViewModel refreshes on open only for a null or RUNNING first value and polls RUNNING every 10 s (max 30); `refresh` always uses `GET /api/user/export` → clear personal files unless READY and unexpired → save server response | [Get personal data export](settings/get-personal-data-export.md) |
| `StartPersonalDataExport` | `POST /api/user/export`; passkey confirmation → clear all personal export JSON files → status cache upsert | [Start personal data export](settings/start-personal-data-export.md) |
| `AcceptLegalDocuments` | `POST /api/user/me/legal`; explicit pending-version acceptance in Settings → Privacy | [Accept legal documents](settings/accept-legal-documents.md) |
| `GetLegalDocumentStatuses` | `flow`: Room statuses; `refresh`: `GET /api/user/me/legal` → transactional Room replacement; Privacy screen review and post-acceptance refresh; dashboard "Action required" row status | [Get legal document statuses](settings/get-legal-document-statuses.md) |
| `GetLegalDocumentVersions` | `GET /api/user/legal` | [Get legal document versions](settings/get-legal-document-versions.md) |
| `GetSettings` | local-first | [Get settings](settings/get-settings.md) |
| `EditProfile` | `PATCH /api/user/me`; contact-conflict error | [Edit profile](settings/edit-profile.md) |
| `GetColorScheme`, `UpdateColorScheme` | local only | [Get / update color scheme](settings/get-color-scheme.md) |
| `GetCrashReportingEnabled`, `UpdateCrashReportingEnabled` | local only, through the platform `CrashReporter` (Firebase Crashlytics); every change deletes unsent reports | [Get / update crash reporting](settings/get-crash-reporting-enabled.md) |
| `GetNotificationSettings` | `GET /api/notifications/settings` | [Get notification settings](settings/get-notification-settings.md) |
| `UpdateNotificationSettings` | `PUT /api/notifications/settings` | [Update notification settings](settings/update-notification-settings.md) |
| `UpdateNotificationToken` | `PUT /api/notifications/{deviceUuid}/token` | [Update notification token](settings/update-notification-token.md) |
| `GetAvailablePasskeys`, `DeletePasskey` | `GET /api/auth/passkey`, `DELETE /api/auth/passkey/{id}` | [Delete passkey / Get available passkeys](settings/delete-passkey.md) |
| `CreateNewPasskey` | `GET /api/auth/passkey/add/challenge`, `POST /api/auth/passkey/add/finish`; typed backend errors | [Create new passkey](settings/create-new-passkey.md) |
| `DeleteAccount` | Action confirmation + `DELETE /api/auth/account`; typed backend errors | [Delete account](settings/delete-account.md) |
| `SendContactForm` | `POST /api/user/contactus` | [Send contact form](settings/send-contact-form.md) |

## Use cases composed of other use cases

| Use case | Calls |
|---|---|
| [Finish sign-up](authorization/finish-sign-up.md), [Sign in](authorization/sign-in.md) | `InitialAppDataFetch.rawFetch` |
| [Resend sign-up code](authorization/resend-sign-up-code.md) | `StartSignUp` |
| [Get signup legal documents](authorization/get-sign-up-legal-documents.md) | `refresh`: `GetLegalDocumentVersions` (cross-feature wrapper); `invoke`: local cache only |
| [Initiate business suspend](authorization/initiate-business-suspend.md) | `SwitchDashboardBusiness`, `RefreshBusinessInfo` (cross-feature wrapper) |
| [Initial app data fetch](authorization/initial-app-data-fetch.md) | `timestampProtectedFetch`: `DeleteExpiredPersonalDataExportFile` and `DeleteExpiredBusinessDataExportFiles` before the throttle, `GetSignUpLegalDocuments.refresh` before authorization check; authorized path: `UserProfileCRUD`, `RefreshBusinessInfo`, `LowPriorityDataFetch` → `GetServices`, `GetServiceGroups`, `GetClientsList`, `GetEmployees`, `IsAppointmentsPluginEnabled`, `GetAppointmentSettings`, `UpdateNotificationToken`, `GetNotificationSettings` |
| [Create business](business/create-business.md) | `RefreshBusinessInfo.refreshAndSelect`, `SwitchDashboardBusiness` |
| [Redeem employee invitation](employees/redeem-employee-invitation.md) | `RefreshBusinessInfo.refreshAndSelect`, `SwitchDashboardBusiness` |
| [Join business](business/join-business.md) | `RedeemEmployeeInvitation` (cross-feature wrapper) |
| [Refresh business info](business/refresh-business-info.md) | `IsAppointmentsPluginEnabled.refresh` per business |
| [Get available dashboard features](business/get-available-dashboard-features.md) | `ObserveDashboardBusinessChanges`, `IsAppointmentsPluginEnabled.flow`, `UserProfileCRUD.observe` |
| [Download business data export](business/download-business-data-export.md) | `GetBusinessDataExport.flow` |
| [Observe dashboard setup status](business/observe-dashboard-setup-status.md) | `BusinessDataSource.observeUserBusinessInfoInDb` (business + selection snapshot), `IsAppointmentsPluginEnabled.flow`, `GetServiceGroups.flow`, `GetServices.flow`, `GetClientsList.flow` (cross-feature) |
| [Refresh dashboard setup status](business/refresh-dashboard-setup-status.md) | `IsAppointmentsPluginEnabled.refresh`, `GetServiceGroups.refresh`, `GetServices.refresh`, `GetClientsList.refresh` (cross-feature) |
| [Get appointment options](appointments/get-appointment-options.md) | `GetClientsList.refresh`, `GetServices.refresh` (parallel) |
| [Observe appointment options](appointments/observe-appointment-options.md) | `GetClientsList.flow`, `GetServices.flow` (combined) |
| [Get assignable services](employees/get-assignable-services.md) | `GetServices` (cross-feature wrapper) |
| [Is business owner](employees/is-business-owner.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Create appointment](appointments/create-appointment.md) | `CreateQuote`, `GetServices.refresh` on mismatch, `ObserveUserBusinessesChanges` for the default employee |
| [Can edit employees](employees/can-edit-employees.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Get clients permissions](clients/get-clients-permissions.md) | `ObserveUserBusinessesChanges`, `UserProfileCRUD.observe` (cross-feature wrapper) |
| [Get services permissions](services/get-services-permissions.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Update employee](employees/update-employee.md) | `IsBusinessOwner` |
| [Is business owner](employees/is-business-owner.md), [Get settings](settings/get-settings.md), [Edit profile](settings/edit-profile.md), [Get notification settings](settings/get-notification-settings.md) | `UserProfileCRUD` |
| [Create new passkey](settings/create-new-passkey.md) | `GetAvailablePasskeys` |
| every `Get*.flow()` list | `ObserveDashboardBusinessChanges` |
| [Delete account](settings/delete-account.md) | `UserProfileCRUD`, `ConfirmSensitiveAction`, `LogOut` |
| [Delete client](clients/delete-client.md) | `ConfirmSensitiveAction` |
| [Start personal data export](settings/start-personal-data-export.md) | `UserProfileCRUD`, `ConfirmSensitiveAction` |
| [Start business data export](business/start-business-data-export.md) | `ConfirmSensitiveAction` |

## Known issues found while documenting

These are recorded here and in the linked diagrams and are currently accepted as expected behaviour.

| Where | Issue |
|---|---|
| [Refresh business info](business/refresh-business-info.md) | Deleting a business the user has left does not remove its appointment rows, which reference the business only logically. The `notification_prefs` pending push token also survives logout. |
| [Redeem employee invitation](employees/redeem-employee-invitation.md) | A refresh or switch failure after a successful redeem surfaces as an error, even though the user has joined. |
| [preferences](../database/preferences.md) | `last_synced_at_*` markers are written by every list refresh but never read. |
| Unused use cases | `EditService`, `IsUserLoggedIn()` (non-flow) and `CreateAppointmentRequest` have no production caller. |
