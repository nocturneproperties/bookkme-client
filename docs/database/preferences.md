[← Local database ER diagrams](README.md)

# Key-value preferences (DataStore)

Small scalar state lives in Jetpack DataStore rather than Room. `PreferenceProvider.get(name)` opens
`<name>.preferences_pb`. A bucket name is owned by exactly one `single`-scoped class and opened once
(DataStore throws if two instances target the same file). See **Prefs buckets** in `AGENTS.md`.

| Bucket | Owner | Keys | Purpose | Cleared on logout |
|---|---|---|---|---|
| `authorization_prefs` | `CommonAuthorizationDataSource` | `access_token`, `refresh_token` (String), `authorized` (Boolean), `last_initial_data_fetch_at` (epoch ms) | Bearer tokens for Ktor's `BearerAuthProvider`. `authorized` drives `IsUserLoggedIn.asFlow()`, which the root navigation observes. `last_initial_data_fetch_at` throttles [initial app data fetch](../operations/authorization/initial-app-data-fetch.md) to once per 30 min. | ✅ `clear()`, then `DELETE /api/auth/session` |
| `device_prefs` | `CommonDeviceDataSource` | `device_uuid` (String) | Stable per-install device id sent on sign-in/sign-up and used for push token registration | ❌ intentionally kept, since it identifies the install and not the user |
| `business_prefs` | `CommonBusinessDataSource` | `dashboard_id` (String uuid) | The currently selected business, which scopes every list screen | ✅ |
| `plugin_prefs` | `CommonPluginDataSource` | `appointment_plugin_available_<businessId>` (Boolean) | Whether the appointments plugin is enabled. It is nullable, so "unknown" and "disabled" are different values. | ✅ |
| `clients_prefs` | `CommonClientsDataSource` | `last_synced_at_<businessId>` (epoch ms) | List sync marker | ✅ |
| `services_prefs` | `ServiceDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `service_groups_prefs` | `ServiceGroupDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `employees_prefs` | `EmployeeDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `employee_invitations_prefs` | `EmployeeInvitationDataSourceImpl` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `appointments_prefs` | `CommonAppointmentDataSource` | `last_synced_at_<businessId>_<yyyy-MM-dd>` | Per-day sync marker | ✅ |
| `appointment_requests_prefs` | `CommonAppointmentRequestDataSource` | `last_synced_at_<businessId>` | List sync marker | ✅ |
| `notification_prefs` | `CommonNotificationSettingsDataSource` | `pending_notification_token` (String) | Push token that failed to upload and is retried by [Update notification token](../operations/settings/update-notification-token.md) | ❌ kept; the datasource's `doOnLogOut()` clears only the `notification_settings` table |
| `settings_prefs` | `CommonSettingsDataSource` | `color_scheme` (Long id) | Light, dark or system theme | ❌ intentionally kept as a device preference |
| `biometry_prefs` | `library/biometry` `BiometryCore` | `biometry_opt_in` (Boolean) | Biometric unlock opt-in | ✅ set to `null` |

The `last_synced_at_*` markers were introduced for the old `cached()` pattern, where they told "synced,
and the list is truly empty" apart from "never synced". Since the `flow()`/`refresh()` migration **no use case
reads them**. `getLastSyncedAt(...)` is still on every list datasource interface, but its only callers are
tests. Every `refresh()` still writes them with `saveLastSyncedAt(...)`, so today they are write-only state.
They can be used again or removed; the empty-state behaviour itself now comes from the presentation layer's
list-loading mechanism.
