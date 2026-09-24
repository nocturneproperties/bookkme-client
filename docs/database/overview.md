[← Local database ER diagrams](README.md)

# Whole-database overview

Solid lines are **real** SQLite foreign keys, all declared `ON DELETE CASCADE`. Every other reference between
tables is a plain `uuid` column with no constraint, a *logical* reference. Most of these point at
denormalized snapshot data, and the others point at rows that may simply not be cached yet. Child tables of an
aggregate (schedules, snapshots, channels) are omitted here; see each feature's diagram.

```mermaid
erDiagram
    BUSINESS ||--o{ CLIENT : "businessId FK"
    BUSINESS ||--o{ EMPLOYEE : "businessId FK"
    BUSINESS ||--o{ EMPLOYEE_INVITATION : "businessId FK"
    BUSINESS ||--o{ SERVICE_GROUP : "businessId FK"
    BUSINESS ||--o{ SERVICE : "businessId FK"
    SERVICE_GROUP ||--o{ SERVICE : "groupId FK"

    BUSINESS |o..o{ APPOINTMENT : "businessId (logical)"
    BUSINESS |o..o{ APPOINTMENT_REQUEST : "businessId (logical)"
    BUSINESS |o..o| APPOINTMENT_SETTINGS : "businessId (logical, UK)"
    CLIENT |o..o{ APPOINTMENT : "clientId (logical, snapshot cols)"
    EMPLOYEE |o..o{ APPOINTMENT : "employeeId (logical, snapshot cols)"
    SERVICE |o..o{ APPOINTMENT : "via appointment_service_snapshot.id"
    SERVICE |o..o{ EMPLOYEE : "via employee_service_snapshot.id"
    USER_PROFILE |o..o| NOTIFICATION_SETTINGS : "userId (logical, UK)"

    BUSINESS {
        uuid id PK
    }
    CLIENT {
        uuid id PK
    }
    EMPLOYEE {
        uuid id PK
    }
    EMPLOYEE_INVITATION {
        uuid id PK
    }
    SERVICE_GROUP {
        uuid id PK
    }
    SERVICE {
        uuid id PK
    }
    APPOINTMENT {
        uuid id PK
    }
    APPOINTMENT_REQUEST {
        uuid id PK
    }
    APPOINTMENT_SETTINGS {
        uuid id PK
    }
    USER_PROFILE {
        uuid id PK
    }
    NOTIFICATION_SETTINGS {
        uuid id PK
    }
```

## Why some references are logical

- **Appointments and requests** keep the client, employee and services as snapshots taken when the backend
  returned them. A client deleted later should not delete the appointment history, and a price change should not
  rewrite past bookings.
- **The appointment feature is a separately enabled plugin** (see [Enable appointments
  plugin](../operations/business/enable-appointments-plugin.md)). Its tables can be populated for a
  business whose other lists were never fetched.
- **Upsert order is not guaranteed across features.** [Low-priority data fetch](../operations/authorization/initial-app-data-fetch.md)
  refreshes services, groups, clients and employees one after another. A hard FK from, say,
  `employee_service_snapshot` to `service` would make the employee insert fail whenever services had not been
  cached yet.

## Cascade consequences

Deleting one `business` row removes all of its clients, employees (and their schedules/snapshots),
invitations, service groups and services. Its appointments, requests and appointment settings **survive**.
Business rows are deleted only on logout (`businessDao.clear()`), which empties every table anyway. The cascades that do fire in practice are `service_group` → `service`
([Delete service group](../operations/services/delete-service-group.md)) and the parent-to-child aggregate
cascades triggered by the `deleteByIds` stale-row deletion inside each `refresh()`.
