# Local database ER diagrams

The app keeps one on-device Room database, `bookk_me.db` (`database/src/commonMain/kotlin/me/bookk/database/AppDatabase.kt`),
shared by every feature. The same schema runs on Android and iOS through the bundled SQLite driver. It is a
**read cache** of what the backend holds: every row gets there after a successful network call, and nothing is
queued offline and pushed later. Screens observe the tables through `Flow` DAO queries, so a write to the
database is what redraws the screen.

The `database` module holds no domain types. Each feature's data module owns the tables it writes and does the
entity ↔ domain mapping itself (`feature/<name>/data/src/commonMain/.../mapping/`). The diagrams below are
grouped by the feature whose datasource **writes** the table.

- [Authorization](authorization.md): `user_profile`
- [Business](business.md): `business` and its schedule tables
- [Clients](clients.md): `client`
- [Services](services.md): `service`, `service_group`
- [Employees](employees.md): `employee` and its schedule and service snapshot tables, `employee_invitation`
- [Appointments](appointments.md): `appointment`, `appointment_request`, `appointment_settings` and their child tables
- [Settings](settings.md): `notification_settings`, `notification_settings_channel`
- [Whole-database overview](overview.md): real foreign keys compared with logical references
- [Key-value preferences (DataStore)](preferences.md): every `PreferenceProvider` bucket, its keys, and whether logout clears it

## Conventions

- **Version and migrations.** The schema is at `version = 16`. Every step from 1 to 16 is a Room
  `AutoMigration`. Only 11 → 12 needs a spec (`DeleteEmployeeInvitationEmail` drops `employee_invitation.email`).
  The builder also sets `fallbackToDestructiveMigration(true)` and `fallbackToDestructiveMigrationOnDowngrade(true)`.
  If a migration is missing, the cache is wiped instead of the app crashing, which is safe only because every
  table can be refetched.
- **Type converters.** `Uuid` → `TEXT`, `Instant` → epoch millis `INTEGER`, `Duration` → `INTEGER`.
  Enums (`status`, `dayOfWeek`, `channel`) are stored as their `name` string. `LocalDate` and `LocalTime`
  values (`start`/`end`/`from`/`to`) are stored as ISO strings.
- **Aggregates are a parent row plus child tables.** A domain aggregate with collections (business schedule,
  employee schedule, appointment services, settings channels) is stored as the parent entity plus one child
  table per collection. Each child table has a composite primary key and a `CASCADE` foreign key to the parent,
  and a `*Local` relation class (`@Embedded` + `@Relation`) reads it back. Writes go through a DAO
  `@Transaction open suspend fun upsertWithChildren(...)` / `upsertAllWithChildren(...)`, which upserts the
  parent, **deletes that parent's existing children**, and re-inserts the new ones. This prevents stale children
  from outliving an edit.
- **Snapshots, not joins.** Appointments, appointment requests and employees embed denormalized copies of the
  services, client and employee they refer to (`*_service_snapshot` tables and the `client*`/`employee*`
  columns). A snapshot records what the backend returned at booking time, so renaming a service does not
  rewrite past appointments.
- **Denormalized permissions.** `business` and `appointment_settings` store the *current user's* permission
  grants as flat `*Permission{View,Update,Delete}` boolean columns. The backend computes them per request (see
  bookk-server `docs/object-permissions.md`). They are refreshed with the row and are not a shared fact about
  the business.
- **List refresh deletes stale rows by id.** Every `refresh()` that caches a list follows the same
  steps: fetch → `get<X>IdsInDb(businessId)` → delete ids the server no longer returned (in chunks of
  `DELETE_CHUNK_SIZE`) → upsert the fresh list → `saveLastSyncedAt`. Only the delete and upsert
  steps touch the table, so observers never see an empty list partway through. See e.g. [Get clients
  list](../operations/clients/get-clients-list.md).
- **Sync markers live in preferences, not tables.** Each list `refresh()` records a
  `last_synced_at_<businessId>` key in the owning datasource's DataStore bucket. Nothing reads these keys any more; see [preferences](preferences.md).

## What logout clears

`LogOut` ([diagram](../operations/authorization/log-out.md)) calls `doOnLogOut()` on every `LogOutAction`
bound in Koin. Each call is wrapped in its own `runCatching`, so one failure doesn't block the rest.

| Table(s) | Cleared by | Cleared on logout? |
|---|---|---|
| `user_profile` | `CommonUserProfileDataSource` | ✅ `profileDao.clear()` |
| `client` | `CommonClientsDataSource` | ✅ |
| `service` | `ServiceDataSourceImpl` | ✅ |
| `service_group` | `ServiceGroupDataSourceImpl` | ✅ (also cascades to `service`) |
| `employee` (+ 4 child tables) | `EmployeeDataSourceImpl` | ✅ |
| `employee_invitation` | `EmployeeInvitationDataSourceImpl` | ✅ |
| `appointment` (+ snapshot) | `CommonAppointmentDataSource` | ✅ |
| `appointment_request` (+ snapshot) | `CommonAppointmentRequestDataSource` | ✅ |
| `business` (+ 3 schedule tables) | `CommonBusinessDataSource` | ✅ `businessDao.clear()` after `business_prefs.clear()`, which also cascades to any client/employee/service rows still left |
| `appointment_settings` (+ 3 child tables) | `CommonAppointmentSettingsDataSource` | ✅ `appointmentSettingsDao.clear()` |
| `notification_settings` (+ channels) | `CommonNotificationSettingsDataSource` | ✅ `notificationSettingsDao.clear()` |

Every table is cleared on logout. [Refresh business info](../operations/business/refresh-business-info.md)
still only upserts, so while signed in, a business the user has left stays in the cache until the next logout.
