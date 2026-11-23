# Local database ER diagrams

There are two on-device Room databases, one per audience. Each app opens exactly one of them:

| Database | Module | File | Platforms | Tables |
|---|---|---|---|---|
| `BusinessDatabase` | `:database:business` | `bookk_me.db` | Android, iOS (bundled SQLite driver) | account tables + every business table below |
| `ClientDatabase` | `:database:client` | `bookk_me_client.db` | Web, the client web app (SQLite WASM worker, see `apps/client/README.md`) | account tables + `business` (with schedules, `dashboard_business`, `business_data_export`) + `service_group` and `service` + every `appointment*` table + `saved_business` |

Every entity, relation, DAO and type converter lives in `:database:schema` (packages `me.bookk.database.entity`,
`relation`, `dao`, `converter`), whichever databases hold the table; the database modules hold only the
`@Database` class, its builders and migrations. Each database has one DAO interface with one Koin module binding its DAOs: `BusinessDaos`
(`businessDaoModule()`) for `BusinessDatabase` and `ClientDaos` (`clientDaoModule()`) for `ClientDatabase`. Both
declare the account DAOs (`profileDao`, `notificationSettingsDao`, `legalDocumentStatusDao`, `personalDataExportDao`),
`businessDao`, `businessDataExportDao`, `serviceDao`, `serviceGroupDao` and the appointment DAOs (`appointmentDao`,
`appointmentRequestDao`, `appointmentSettingsDao`); only `BusinessDaos` has `businessLinkDao`, `clientDao`, `employeeDao` and `employeeInvitationDao`, and only `ClientDaos` has `savedBusinessDao`. `business_link`, `client` and the `employee*` tables are therefore in `BusinessDatabase` only, and `saved_business` (the client's saved businesses, written by `appointments/data`) in `ClientDatabase` only.
Each database lists the entities it holds in its own `@Database`, implements its interface
and includes its module, so each database file holds its own copy of the tables, and foreign keys and
cascades stay inside that database. `BusinessDatabase` implements `BusinessDaos`; `ClientDatabase` implements `ClientDaos`. Data modules depend on `:database:schema` alone and get
DAOs from Koin, so `authorization/data`, `settings/data`, `business/data`, `appointments/data` and `services/data` run on both
apps; `clients/data` and `employees/data` are mobile-only. Changing a shared entity changes the schema of every database
that lists it, so each one needs its own version bump and migration.

Room generates the DAO implementations (for example `UserProfileDao_Impl` in the DAO package) inside
each database module. So no target may depend on both `:database:business` and `:database:client`, or the
generated classes collide.

Both databases are a **read cache** of what the backend holds: every row gets there after a successful network
call, and nothing is queued offline and pushed later. Screens observe the tables through `Flow` DAO queries,
so a write to the database is what redraws the screen.

The database modules hold no domain types. Each feature's data module owns the tables it writes and does the
entity ↔ domain mapping itself (`feature/<name>/data/src/commonMain/.../mapping/`). The diagrams below are
grouped by the feature whose datasource **writes** the table. They describe `BusinessDatabase`. `ClientDatabase`
holds the account tables from [Authorization](authorization.md) and [Settings](settings.md) and the
[Business](business.md) and [Appointments](appointments.md) tables, with the same columns.

- [Authorization](authorization.md): `user_profile`
- [Business](business.md): `business`, its schedule tables, `dashboard_business` (the selected business), `business_data_export` and `business_link`
- [Clients](clients.md): `client`
- [Services](services.md): `service`, `service_group`
- [Employees](employees.md): `employee` and its schedule and service snapshot tables, `employee_invitation`
- [Appointments](appointments.md): `appointment`, `appointment_request`, `appointment_settings` and their child tables; `saved_business` (`ClientDatabase` only)
- [Settings](settings.md): `notification_settings`, `notification_settings_channel`, `legal_document_status`, `personal_data_export`
- [Whole-database overview](overview.md): real foreign keys compared with logical references
- [Key-value preferences (DataStore)](preferences.md): every `PreferenceProvider` bucket, its keys, and whether logout clears it

## Conventions

- **Storage and other tabs (`ClientDatabase`).** `DatabaseStorage` (`:database:schema`, bound by both database
  modules) reports whether the main database is a file (`pragma_database_list`); the web worker falls back to an
  in-memory database when it gets no OPFS storage, and `InitialAppDataFetch` then fetches on every start. Room
  tracks changes with `TEMP` triggers on its own connection, so it never sees a commit another tab makes to the
  shared OPFS file. The worker posts `changed` on the `bookkme-db-changes` `BroadcastChannel` once a transaction
  that changed rows is committed (`sqlite3_total_changes` moved), not after every statement that merely is not
  read-only: Room's own invalidation refresh runs `BEGIN IMMEDIATE … END` without changing anything, and the tab
  hears its own announcements, so announcing those would re-trigger the check forever. `CrossTabInvalidation` (`:database:client` `jsMain`, started when the database is created) runs
  `ExternalChangeInvalidator`: when `PRAGMA data_version` moved (another connection committed), it sets
  `room_table_modification_log.invalidated = 1` and calls `invalidationTracker.refreshAsync()`, so every observed
  `flow()` re-queries. `ExternalChangeInvalidatorTest` (two Room instances on one file) pins Room's internal table
  name.
- **Version and migrations.** Each database has its own version and migration history. `BusinessDatabase` is
  at `version = 27`; it kept the schema history of the former single `AppDatabase` (same file, same schema), so
  the split needed no migration. `ClientDatabase` is at `version = 4`: 1 held the account tables, the additive 1 → 2 auto-migration adds the business and appointment tables, the additive 2 → 3 auto-migration adds `saved_business`, and the additive 3 → 4 auto-migration adds `service_group` and `service` for the Mini App's services step. Moving `service`/`service_group` into `:database:schema` changed no table of `BusinessDatabase`, which stayed at 27. Moving entities into `:database:schema` changed no table, so `BusinessDatabase` stayed at 26.
  In `BusinessDatabase`, every step from 1 to 22 is a Room `AutoMigration`. Only 11 → 12 needs a spec (`DeleteEmployeeInvitationEmail` drops `employee_invitation.email`).
  The manual 22 → 23 migration (`DefaultBusinessRetention`) makes business retention non-null,
  backfills missing values with 36 months and preserves custom periods and dependent rows.
  The additive 23 → 24 auto-migration adds the `legal_document_status` table.
  The additive 24 → 25 auto-migration adds the singleton `personal_data_export` table.
  The additive 25 → 26 auto-migration adds the per-business `business_data_export` table.
  The additive 26 → 27 auto-migration adds the per-business `business_link` table.
  Both builders set `fallbackToDestructiveMigration(true)` and `fallbackToDestructiveMigrationOnDowngrade(true)`.
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
  `last_synced_at_<businessId>` key in the owning datasource's DataStore bucket. Those timestamps are currently write-only.
  See [preferences](preferences.md). Legal-document statuses need no sync marker and observe Room directly.

## What logout clears

`LogOut` ([diagram](../operations/authorization/log-out.md)) calls `doOnLogOut()` on every `LogOutAction`
bound in Koin. Each call is wrapped in its own `runCatching`, so one failure doesn't block the rest.

| Table(s) | Cleared by | Cleared on logout? |
|---|---|---|
| `user_profile` | `CommonUserProfileDataSource` | ✅ `profileDao.clear()` |
| `signup_legal_documents_prefs` (public legal metadata) | No `LogOutAction` binding for `CommonSignUpLegalDocumentsDataSource` | ❌ intentionally retained for cache-only signup |
| In-memory sign-up session (user data, challenge, attestation, rejection; never persisted) | `InMemorySignUpSessionDataSource` | ✅ `clear()` |
| In-memory booking preparation (the client web app's approved services per business; never persisted) | `InMemoryBookingPreparationDataSource` | ✅ every business's services |
| `client` | `CommonClientsDataSource` | ✅; confirmed deletion also evicts associated appointment/request snapshots |
| `service` (both databases) | `ServiceDataSourceImpl` | ✅ |
| `service_group` (both databases) | `ServiceGroupDataSourceImpl` | ✅ (also cascades to `service`) |
| `employee` (+ 4 child tables) | `EmployeeDataSourceImpl` | ✅ |
| `employee_invitation` | `EmployeeInvitationDataSourceImpl` | ✅ |
| `appointment` (+ service snapshot, adjustment services) | `CommonAppointmentDataSource` | ✅ `appointmentDao.clear()`, including single-appointment refresh results and `clientUserId`; children cascade |
| `appointment_request` (+ snapshot) | `CommonAppointmentRequestDataSource` | ✅ including `clientUserId` |
| `business` (+ 3 schedule tables, `dashboard_business`) | `CommonBusinessDataSource` | ✅ `clearDashboardBusiness()` then `businessDao.clear()` (including retention, default 36 months), which also cascades to any client/employee/service rows still left |
| `appointment_settings` (+ 3 child tables) | `CommonAppointmentSettingsDataSource` | ✅ `appointmentSettingsDao.clear()` |
| `legal_document_status` | `CommonLegalDocumentsDataSource` | ✅ `legalDocumentStatusDao.clear()` |
| `personal_data_export` and cached JSON (`caches/personal_data_exports/`, outside Room) | `CommonDataExportDataSource` | ✅ `personalDataExportDao.clear()` and personal export directory removed through `FileProvider.deleteRecursively()`; the directory is also cleared when a new file is saved, on export start and on refreshes returning a non-downloadable status; other export directories are unaffected |
| `business_data_export` and cached JSON (`caches/business_data_exports/`, outside Room) | `CommonDataExportDataSource` (business data module) | ✅ `businessDataExportDao.clear()` and the whole business export directory removed through `FileProvider.deleteRecursively()`; a business's directory is also cleared when a new file is saved, on export start, on refreshes returning a non-downloadable status and when the backend reports the caller no longer owns the business (its row is deleted too); personal export files are unaffected |
| `notification_settings` (+ channels) | `CommonNotificationSettingsDataSource` | ✅ `notificationSettingsDao.clear()` |
| `business_link` | `CommonBusinessLinkDataSource` | ✅ `businessLinkDao.clear()`; a business's row is also deleted when the backend reports it has no link or the caller no longer owns it |
| `saved_business` (`ClientDatabase` only) and, on the web, `appointment`/`appointment_request` | `CommonClientBookingDataSource` (bound by `clientAppointmentsDataModule()`, client web app only) | ✅ `appointmentDao.clear()`, `appointmentRequestDao.clear()`, `savedBusinessDao.clear()` |

Every table is cleared on logout by the data source that writes it, which is bound as a `LogOutAction` in its
feature's data module. Shared tables (account, business and appointment tables) are cleared through the shared
DAOs, so the same data sources clear them in whichever database the app installed. On the web, `ClientDatabase`
has the business and appointment tables. The client web app installs only `clientAppointmentsDataModule()`, whose
`CommonClientBookingDataSource` writes the client's upcoming bookings into `appointment`/`appointment_request` and
clears them and `saved_business` on logout, and `clientBusinessDataModule()`, whose `CommonBusinessDataSource`
writes the businesses the client booked with or saved ([Get businesses](../operations/business/get-businesses.md))
into `business` and clears the business tables on logout, and the full `servicesDataModule()`, whose `ServiceDataSourceImpl`/`ServiceGroupDataSourceImpl` write the services of a business the client opens ([Get business services](../operations/services/get-business-services.md), called by booking's `GetBookingCatalog` and `GetBookingPreparation`) into `service`/`service_group` and clear them and their `services_prefs`/`service_groups_prefs` markers on logout. It does not install the full business modules, whose
`RefreshBusinessInfo` would delete every business missing from `/business/mine`. While signed in, [Refresh business info](../operations/business/refresh-business-info.md)
deletes businesses the user no longer has, together with their cascaded rows.
