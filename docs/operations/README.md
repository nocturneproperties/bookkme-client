# Operation activity diagrams

One Mermaid flowchart per domain use case (`feature/<name>/domain/api`, implemented in `domain/impl`),
grouped by feature. The mobile client has no routes of its own. A use case is the unit that turns a UI action
into network calls, local database or DataStore writes, and in-process events, so each diagram shows those
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

- [Authorization](#authorization)
- [Business](#business)
- [Clients](#clients)
- [Services](#services)
- [Employees](#employees)
- [Appointments](#appointments)
- [Settings](#settings)
- [In-process event map](#in-process-event-map)
- [Use cases composed of other use cases](#use-cases-composed-of-other-use-cases)
- [Known issues found while documenting](#known-issues-found-while-documenting)

## Authorization

| Use case | Network | Diagram |
|---|---|---|
| `CreateAccount` | `POST /api/auth/passkey/sign_up/challenge`, `POST /api/auth/sign_up` | [Create account](authorization/create-account.md) |
| `SignIn` | `GET /api/auth/passkey/sign_in/challenge`, `POST /api/auth/sign_in` | [Sign in](authorization/sign-in.md) |
| `InitiateBusinessSuspend` | via `SwitchDashboardBusiness(null)` (local only) and `RefreshBusinessInfo` (`GET /api/business`) | [Initiate business suspend](authorization/initiate-business-suspend.md) |
| `InitialAppDataFetch` (+ internal `LowPriorityDataFetch`) | `GET /api/user/me`, `GET /api/business`, then per-feature refreshes | [Initial app data fetch](authorization/initial-app-data-fetch.md) |
| `LogOut` | `DELETE /api/auth/session` | [Log out](authorization/log-out.md) |
| `RefreshToken` | `POST /api/auth/refresh` | [Refresh token](authorization/refresh-token.md) |
| `UserProfileCRUD` | `GET` / `PATCH /api/user/me` | [User profile CRUD](authorization/user-profile-crud.md) |
| `IsUserLoggedIn`, `GetTokenInfo`, `GetSettingsColorScheme` | local only | [Is user logged in / Get token info / Get settings color scheme](authorization/is-user-logged-in.md) |

## Business

| Use case | Network | Diagram |
|---|---|---|
| `CreateBusiness` | `POST /api/business` | [Create business](business/create-business.md) |
| `UpdateBusiness` | `PUT /api/business/{id}` | [Update business](business/update-business.md) |
| `RefreshBusinessInfo` | `GET /api/business` | [Refresh business info](business/refresh-business-info.md) |
| `SwitchDashboardBusiness` | `PUT /api/business/{id}/dashboard` (best-effort) | [Switch dashboard business](business/switch-dashboard-business.md) |
| `JoinBusiness` | via `RedeemEmployeeInvitation` | [Join business](business/join-business.md) |
| `EnableAppointmentsPlugin` | `POST /api/appointments/enabled/{businessId}` | [Enable appointments plugin](business/enable-appointments-plugin.md) |
| `IsAppointmentsPluginEnabled` | `GET /api/appointments/enabled/{businessId}` | [Is appointments plugin enabled](business/is-appointments-plugin-enabled.md) |
| `CanEditBusiness` | local only (`business` table) | [Can edit business](business/can-edit-business.md) |
| `GetAvailableDashboardFeatures` | local only | [Get available dashboard features](business/get-available-dashboard-features.md) |
| `ObserveDashboardSetupStatus` | local only | [Observe dashboard setup status](business/observe-dashboard-setup-status.md) |
| `ObserveDashboardBusinessChanges`, `ObserveDashboardBusinessIdChanges`, `ObserveCurrentBusinessId` (×4) | local only | [Observe dashboard business](business/observe-dashboard-business-changes.md) |
| `ObserveUserBusinessesChanges` | local only | [Observe user businesses](business/observe-user-businesses-changes.md) |

## Clients

| Use case | Network | Diagram |
|---|---|---|
| `GetClientsList` | `GET /api/business/{businessId}/clients` | [Get clients list](clients/get-clients-list.md) (reference list steps) |
| `GetClient` | local only | [Get client](clients/get-client.md) |
| `CreateClient` | `POST /api/business/{businessId}/clients` | [Create client](clients/create-client.md) |
| `EditClient` | `PATCH /api/business/{businessId}/clients/{id}` | [Edit client](clients/edit-client.md) |
| `DeleteClient` | `DELETE /api/business/{businessId}/clients/{id}` | [Delete client](clients/delete-client.md) |
| `GetClientsPermissions` | local only (`business` table via `ObserveUserBusinessesChanges`) | [Get clients permissions](clients/get-clients-permissions.md) |

## Services

| Use case | Network | Diagram |
|---|---|---|
| `GetServices` | `GET /api/business/{businessId}/service` | [Get services](services/get-services.md) |
| `CreateService` | `POST /api/business/{businessId}/service` | [Create service](services/create-service.md) |
| `EditService` | `PUT /api/business/{businessId}/service/{id}` | [Edit service](services/edit-service.md) |
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
| `GetAppointmentsForBusiness` | `GET /api/appointments/list/{businessId}?date=` | [Get appointments for business](appointments/get-appointments-for-business.md) |
| `GetAppointment` | local only | [Get appointment](appointments/get-appointment.md) |
| `GetAppointmentHistory` | `GET /api/appointments/history/{businessId}` (paged) | [Get appointment history](appointments/get-appointment-history.md) |
| `CreateAppointment` | `POST /api/appointments/instant` | [Create appointment](appointments/create-appointment.md) |
| `UpdateAppointment` | `PUT /api/appointments/{id}` | [Update appointment](appointments/update-appointment.md) |
| `CancelAppointment` | `POST /api/appointments/{id}/cancel` | [Cancel appointment](appointments/cancel-appointment.md) |
| `GetAppointmentRequests` | `GET /api/appointments/request/{businessId}` | [Get appointment requests](appointments/get-appointment-requests.md) |
| `ApproveAppointmentRequest` | `POST /api/appointments` | [Approve appointment request](appointments/approve-appointment-request.md) |
| `DeclineAppointmentRequest` | `POST /api/appointments/request/{id}/decline` | [Decline appointment request](appointments/decline-appointment-request.md) |
| `GetAppointmentSettings` | `GET /api/appointments/settings/{businessId}` | [Get appointment settings](appointments/get-appointment-settings.md) |
| `UpdateAppointmentSettings` | `PUT /api/appointments/settings/{businessId}` | [Update appointment settings](appointments/update-appointment-settings.md) |
| `GetAppointmentOptions` | composes three refreshes | [Get appointment options](appointments/get-appointment-options.md) |

## Settings

| Use case | Network | Diagram |
|---|---|---|
| `GetSettings` | local-first | [Get settings](settings/get-settings.md) |
| `EditProfile` | `PATCH /api/user/me` | [Edit profile](settings/edit-profile.md) |
| `GetColorScheme`, `UpdateColorScheme` | local only | [Get / update color scheme](settings/get-color-scheme.md) |
| `GetNotificationSettings` | `GET /api/notifications/settings` | [Get notification settings](settings/get-notification-settings.md) |
| `UpdateNotificationSettings` | `PUT /api/notifications/settings` | [Update notification settings](settings/update-notification-settings.md) |
| `UpdateNotificationToken` | `PUT /api/notifications/{deviceUuid}/token` | [Update notification token](settings/update-notification-token.md) |
| `GetAvailablePasskeys`, `DeletePasskey` | `GET /api/auth/passkey`, `DELETE /api/auth/passkey/{id}` | [Delete passkey / Get available passkeys](settings/delete-passkey.md) |
| `CreateNewPasskey` | `GET /api/auth/passkey/add/challenge`, `POST /api/auth/passkey/add/finish` | [Create new passkey](settings/create-new-passkey.md) |
| `DeleteAccount` | `DELETE /api/auth/account` | [Delete account](settings/delete-account.md) |
| `SendContactForm` | `POST /api/user/contactus` | [Send contact form](settings/send-contact-form.md) |

## In-process event map

Some features publish domain events on a package-level `MutableSharedFlow` (`extraBufferCapacity = 100`,
`DROP_OLDEST`). Subscribers use `listenFor<T>()`. These events are for work that a Room observer cannot do,
such as refetching a *different* list or re-rendering a screen that holds a non-observable copy. Most screens
don't need them, because they redraw from the table.

| Event | Producer(s) | Consumer(s) |
|---|---|---|
| `AppointmentEvent.Created` | [Create appointment](appointments/create-appointment.md), [Approve appointment request](appointments/approve-appointment-request.md) | `AppointmentListViewModel.reload()` |
| `AppointmentEvent.Updated` | [Update appointment](appointments/update-appointment.md) | `AppointmentListViewModel.reload()` |
| `AppointmentEvent.Cancelled` | [Cancel appointment](appointments/cancel-appointment.md) | none |
| `ClientEvent.Created` | [Create client](clients/create-client.md) | none |
| `ClientEvent.Updated` | [Edit client](clients/edit-client.md) | `ClientDetailsViewModel` |
| `ClientEvent.Deleted` | [Delete client](clients/delete-client.md) | none |

## Use cases composed of other use cases

| Use case | Calls |
|---|---|
| [Create account](authorization/create-account.md), [Sign in](authorization/sign-in.md) | `InitialAppDataFetch.rawFetch` |
| [Initiate business suspend](authorization/initiate-business-suspend.md) | `SwitchDashboardBusiness`, `RefreshBusinessInfo` (cross-feature wrapper) |
| [Initial app data fetch](authorization/initial-app-data-fetch.md) | `UserProfileCRUD`, `RefreshBusinessInfo`, `LowPriorityDataFetch` → `GetServices`, `GetServiceGroups`, `GetClientsList`, `GetEmployees`, `IsAppointmentsPluginEnabled`, `GetAppointmentSettings`, `UpdateNotificationToken`, `GetNotificationSettings` |
| [Create business](business/create-business.md) | `RefreshBusinessInfo`, `SwitchDashboardBusiness` |
| [Redeem employee invitation](employees/redeem-employee-invitation.md) | `RefreshBusinessInfo`, `SwitchDashboardBusiness` |
| [Join business](business/join-business.md) | `RedeemEmployeeInvitation` (cross-feature wrapper) |
| [Refresh business info](business/refresh-business-info.md) | `IsAppointmentsPluginEnabled.refresh` per business |
| [Get available dashboard features](business/get-available-dashboard-features.md) | `ObserveDashboardBusinessChanges`, `IsAppointmentsPluginEnabled.flow` |
| [Observe dashboard setup status](business/observe-dashboard-setup-status.md) | `ObserveDashboardBusinessChanges`, `IsAppointmentsPluginEnabled.flow` |
| [Get appointment options](appointments/get-appointment-options.md) | `GetClientsList`, `GetServices`, `GetAppointmentSettings` (parallel) |
| [Get assignable services](employees/get-assignable-services.md) | `GetServices` (cross-feature wrapper) |
| [Is business owner](employees/is-business-owner.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Can edit employees](employees/can-edit-employees.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Get clients permissions](clients/get-clients-permissions.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Get services permissions](services/get-services-permissions.md) | `ObserveUserBusinessesChanges` (cross-feature wrapper) |
| [Update employee](employees/update-employee.md) | `IsBusinessOwner` |
| [Create appointment](appointments/create-appointment.md), [Is business owner](employees/is-business-owner.md), [Get settings](settings/get-settings.md), [Edit profile](settings/edit-profile.md), [Get notification settings](settings/get-notification-settings.md) | `UserProfileCRUD` |
| [Create new passkey](settings/create-new-passkey.md) | `GetAvailablePasskeys` |
| every `Get*.flow()` list | `ObserveDashboardBusinessChanges` |

## Known issues found while documenting

These are recorded here and in the linked diagrams and are currently accepted as expected behaviour.

| Where | Issue |
|---|---|
| [Update appointment](appointments/update-appointment.md) | Returns the input value, not the server result. |
| [Refresh business info](business/refresh-business-info.md) | Deleting a business the user has left does not remove its appointment rows, which reference the business only logically. The `notification_prefs` pending push token also survives logout. |
| [Create business](business/create-business.md) | Currency hard-coded to `UAH`. |
| [Cancel appointment](appointments/cancel-appointment.md) | The datasource method does both the network call and the DB write. |
| [Redeem employee invitation](employees/redeem-employee-invitation.md) | A refresh or switch failure after a successful redeem surfaces as an error, even though the user has joined. |
| [preferences](../database/preferences.md) | `last_synced_at_*` markers are written by every list refresh but never read. |
| Unused use cases | `CreateQuote`, `EditService` and `IsUserLoggedIn()` (non-flow) have no production caller. `AppointmentRequestDataSource.createAppointmentRequest` has no use case. |
