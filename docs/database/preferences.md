[← Local database ER diagrams](README.md)

# Key-value preferences (DataStore)

Small scalar state lives in Jetpack DataStore rather than Room. `PreferenceProvider.get(name)` opens
`<name>.preferences_pb`. A bucket name is owned by exactly one `single`-scoped class and opened once
(DataStore throws if two instances target the same file). See **Prefs buckets** in `AGENTS.md`.
On the website (JS), each bucket is the localStorage entry `<name>.preferences_pb`, plus DataStore's
`datastore_LOCAL_<name>.preferences_pb_version` counter. Open tabs pick up each other's writes live.

| Bucket | Owner | Keys | Purpose | Cleared on logout |
|---|---|---|---|---|
| `authorization_prefs` | `CommonAuthorizationDataSource` | `access_token`, `refresh_token` (String), `authorized` (Boolean), `last_initial_data_fetch_at` (epoch ms) | Bearer tokens for Ktor's `BearerAuthProvider`. `authorized` drives `IsUserLoggedIn.asFlow()`, which the root navigation observes. `last_initial_data_fetch_at` throttles [initial app data fetch](../operations/authorization/initial-app-data-fetch.md) to once per 30 min. | ✅ `clear()`, then `DELETE /api/auth/session` |
| `account_deletion_prefs` | `CommonAccountDeletionDataSource` | `deletes_at` (epoch ms) | When the account deletion the person requested runs (backend 7-day grace period). Written from the `DELETE /api/auth/account` answer and by [Get account deletion](../operations/settings/get-account-deletion.md)'s refresh (`GET /api/auth/account/deletion`, removed when none is pending); read by Delete account to show the pending deletion offline | ✅ `clear()` |
| `signup_legal_documents_prefs` | `CommonSignUpLegalDocumentsDataSource` | `documents` (JSON String: privacyPolicyVersion, privacyPolicyUrl, termsOfServiceVersion, termsOfServiceUrl) | Complete public legal-document snapshot preloaded during initial fetch and read locally by signup; survives app restarts | ❌ intentionally retained across logout: public document metadata, not user consent |
| `device_prefs` | `CommonDeviceDataSource` | `device_uuid` (String) | Stable per-install device id sent on sign-in/sign-up and used for push token registration | ❌ intentionally kept, since it identifies the install and not the user |
| `plugin_prefs` | `CommonPluginDataSource` | `appointment_plugin_available_<businessId>` (Boolean) | Whether the appointments plugin is enabled. It is nullable, so "unknown" and "disabled" are different values. | ✅ |
| `clients_prefs` | `CommonClientsDataSource` | `last_synced_at_<businessId>` (epoch ms) | List sync marker | ✅ |
| `services_prefs` | `ServiceDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker (business app and client web app) | ✅ |
| `service_groups_prefs` | `ServiceGroupDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker (business app and client web app) | ✅ |
| `employees_prefs` | `EmployeeDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `employee_invitations_prefs` | `EmployeeInvitationDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `appointments_prefs` | `CommonAppointmentDataSource` | `last_synced_at_<businessId>_<yyyy-MM-dd>` | Per-day sync marker | ✅ |
| `appointment_requests_prefs` | `CommonAppointmentRequestDataSource` | `last_synced_at_<businessId>` | List sync marker (business app; the client web app opens the bucket but never writes it) | ✅ |
| `notification_prefs` | `CommonNotificationSettingsDataSource` | `notification_token` (String) | Latest push token the platform issued, uploaded again by [Update notification token](../operations/settings/update-notification-token.md) on every background data fetch and when push is switched on | ❌ intentionally kept: the token belongs to the app install, and the backend starts every login's device row without one; the datasource's `doOnLogOut()` clears only the `notification_settings` table |
| `settings_prefs` | `CommonSettingsDataSource` | `color_scheme` (Long id) | Light, dark or system theme | ❌ intentionally kept as a device preference |
| `biometry_prefs` | `library/biometry` `BiometryCore` | `biometry_opt_in` (Boolean) | Biometric unlock opt-in | ✅ set to `null` |

The `last_synced_at_*` markers were introduced for the old `cached()` pattern, where they told "synced,
and the list is truly empty" apart from "never synced". Since the `flow()`/`refresh()` migration **no use case
reads them**. `getLastSyncedAt(...)` is still on every list datasource interface, but its only callers are
tests. Every `refresh()` still writes them with `saveLastSyncedAt(...)`, so today they are write-only state.
They can be used again or removed; the empty-state behaviour itself now comes from the presentation layer's
list-loading mechanism.

Personal export status and its timestamps use the singleton `personal_data_export` Room table,
not a preferences bucket or serialized JSON. Row absence distinguishes an unknown status from a
confirmed `NONE` result. Logout clears the table. See [Settings](settings.md). Business export
statuses follow the same rule in the per-business `business_data_export` table; see [Business](business.md).
