[← Local database ER diagrams](README.md)

# Business

Every business the user owns or works at, including the requesting user's permission grants for each one.
Written by `CommonBusinessDataSource`. The currently selected ("dashboard") business id is **not** stored
here. It lives in the `business_prefs` DataStore bucket (`dashboard_id`, see [preferences](preferences.md)).

`business` is the root of most foreign keys in the database. Deleting a business row cascades to its
schedule tables and to `client`, `employee`, `employee_invitation`, `service` and `service_group`
(see [overview](overview.md)).

```mermaid
erDiagram
    BUSINESS ||--o{ BUSINESS_DAY_SCHEDULE : "has (CASCADE)"
    BUSINESS ||--o{ BUSINESS_WORKING_TIME : "has (CASCADE)"
    BUSINESS ||--o{ BUSINESS_DAY_OFF : "has (CASCADE)"

    BUSINESS {
        uuid id PK "logical FK -> backend business.id"
        string name
        string description
        string address
        double locationLat "nullable"
        double locationLng "nullable"
        string currencyCode "ISO 4217; read by GetBusinessCurrency"
        string phone "nullable"
        string insta "nullable"
        string viber "nullable"
        string whatsApp "nullable"
        string telegram "nullable"
        string timeZone
        bool businessPermissionView "current user's grant; default 0"
        bool businessPermissionUpdate
        bool businessPermissionDelete
        bool employeesPermissionView
        bool employeesPermissionUpdate
        bool employeesPermissionDelete
        bool clientsPermissionView
        bool clientsPermissionUpdate
        bool clientsPermissionDelete
        bool servicesPermissionView
        bool servicesPermissionUpdate
        bool servicesPermissionDelete
        bool appointmentsPermissionView
        bool appointmentsPermissionUpdate
        bool appointmentsPermissionDelete
        uuid ownerId "nullable; user id of the business owner"
    }

    BUSINESS_DAY_SCHEDULE {
        uuid businessId PK,FK
        string dayOfWeek PK "DayOfWeek.name"
        bool isActive
    }

    BUSINESS_WORKING_TIME {
        uuid businessId PK,FK
        string dayOfWeek PK
        string from PK "LocalTime ISO"
        string to PK "LocalTime ISO"
    }

    BUSINESS_DAY_OFF {
        uuid businessId PK,FK
        string start PK "LocalDate ISO"
        string end PK "LocalDate ISO"
    }
```

- Written by: [Refresh business info](../operations/business/refresh-business-info.md) (`upsertAllWithChildren`
  for the whole list) and [Update business](../operations/business/update-business.md) (`upsertWithChildren` for one).
- Read by: [Observe dashboard business changes](../operations/business/observe-dashboard-business-changes.md)
  (which many features use to scope their lists), [Observe user businesses
  changes](../operations/business/observe-user-businesses-changes.md), [Update business](../operations/business/update-business.md)
  (reads the current row before merging the edit), [Get business currency](../operations/services/get-business-currency.md)
  and [Is business owner](../operations/employees/is-business-owner.md) (compares `ownerId` with an employee's `userId`).
- Cleared on logout: yes (`businessDao.clear()`). Businesses the user has left are not removed by a refresh,
  which only upserts, so they stay until the next logout.
- Migration notes: 15 → 16 added the nullable `business.ownerId` column (auto-migration). Rows cached before
  the migration keep `NULL` until the next [Refresh business info](../operations/business/refresh-business-info.md).
