[← Local database ER diagrams](README.md)

# Employees

Staff of a business, including each person's schedule and the services they can provide. Also stores the
invitations the business has issued. Written by `EmployeeDataSourceImpl` and `EmployeeInvitationDataSourceImpl`.

```mermaid
erDiagram
    BUSINESS ||--o{ EMPLOYEE : "employs (CASCADE)"
    BUSINESS ||--o{ EMPLOYEE_INVITATION : "issues (CASCADE)"
    EMPLOYEE ||--o{ EMPLOYEE_DAY_SCHEDULE : "has (CASCADE)"
    EMPLOYEE ||--o{ EMPLOYEE_WORKING_TIME : "has (CASCADE)"
    EMPLOYEE ||--o{ EMPLOYEE_DAY_OFF : "has (CASCADE)"
    EMPLOYEE ||--o{ EMPLOYEE_SERVICE_SNAPSHOT : "can provide (CASCADE)"

    BUSINESS {
        uuid id PK
    }

    EMPLOYEE {
        uuid id PK
        uuid businessId FK "indexed"
        string name
        string lastName
        string phone "nullable"
        string email "nullable"
        uuid userId "logical FK -> backend user.profile.id"
        instant createdAt
    }

    EMPLOYEE_DAY_SCHEDULE {
        uuid employeeId PK,FK
        string dayOfWeek PK
        bool isActive
    }

    EMPLOYEE_WORKING_TIME {
        uuid employeeId PK,FK
        string dayOfWeek PK
        string from PK
        string to PK
    }

    EMPLOYEE_DAY_OFF {
        uuid employeeId PK,FK
        string start PK
        string end PK
    }

    EMPLOYEE_SERVICE_SNAPSHOT {
        uuid employeeId PK,FK
        uuid id PK "logical FK -> service.id (not enforced)"
        uuid businessId
        uuid groupId "logical FK -> service_group.id"
        uuid groupBusinessId
        string groupName
        instant groupCreatedAt
        string name
        duration duration
        string priceCurrency
        long priceValue
        bool isAvailable
        instant createdAt
    }

    EMPLOYEE_INVITATION {
        uuid id PK
        uuid businessId FK "indexed"
        uuid invitedBy "logical FK -> backend user.profile.id"
        string code "nullable; plaintext only right after creation, null once processed"
        string status "EmployeeInvitationStatus.name"
        instant createdAt
    }
```

`employee_service_snapshot` is a full copy of the service **and** its group. It deliberately has no foreign key
to `service`, so employees can be cached before services are, or without them.

- Written by: [Get employees](../operations/employees/get-employees.md) `refresh()` (`upsertAllWithChildren`)
  and [Get employee invitations](../operations/employees/get-employee-invitations.md) `refresh()`.
  None of the mutating employee use cases ([Update employee](../operations/employees/update-employee.md),
  [Promote](../operations/employees/promote-employee.md), [Set permission](../operations/employees/set-employee-permission.md),
  [Create](../operations/employees/create-employee-invitation.md) or [Revoke invitation](../operations/employees/revoke-employee-invitation.md))
  writes to the database. The cache only catches up on the next `refresh()`.
- Sync markers: `employees_prefs` and `employee_invitations_prefs` / `last_synced_at_<businessId>`.
- Cleared on logout: yes.
- Migration note: 11 → 12 dropped `employee_invitation.email` (`DeleteEmployeeInvitationEmail` spec).
