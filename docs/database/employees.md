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
        bool businessPermissionView "default 0"
        bool businessPermissionUpdate "default 0"
        bool businessPermissionDelete "default 0"
        bool employeesPermissionView "default 0"
        bool employeesPermissionUpdate "default 0"
        bool employeesPermissionDelete "default 0"
        bool clientsPermissionView "default 0"
        bool clientsPermissionUpdate "default 0"
        bool clientsPermissionDelete "default 0"
        bool servicesPermissionView "default 0"
        bool servicesPermissionUpdate "default 0"
        bool servicesPermissionDelete "default 0"
        bool appointmentsPermissionView "default 0"
        bool appointmentsPermissionUpdate "default 0"
        bool appointmentsPermissionDelete "default 0"
        instant suspendedAt "nullable, null = active"
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

The `*Permission*` columns flatten the employee's `BusinessPermissions` (view/update/delete per resource), in
the same way as the permission columns on `business`. `suspendedAt` is the moment the owner suspended the
employee, or `null` while the employee is active (`Employee.isSuspended`).

- Written by: [Get employees](../operations/employees/get-employees.md) `refresh()` (`upsertAllWithChildren`),
  [Update employee](../operations/employees/update-employee.md) and
  [Set employee suspension](../operations/employees/set-employee-suspension.md) (both upsert the returned employee) and
  [Get employee invitations](../operations/employees/get-employee-invitations.md) `refresh()`.
  [Create](../operations/employees/create-employee-invitation.md) and [Revoke invitation](../operations/employees/revoke-employee-invitation.md)
  do not write to the database. The invitation cache only catches up on the next `refresh()`.
- Read by id: [Get employee](../operations/employees/get-employee.md) (`employeeDao.getEmployee`).
- Sync markers: `employees_prefs` and `employee_invitations_prefs` / `last_synced_at_<businessId>`.
- Cleared on logout: yes.
- Migration notes: 11 → 12 dropped `employee_invitation.email` (`DeleteEmployeeInvitationEmail` spec).
  14 → 15 added the 15 `employee.*Permission*` columns (auto-migration, all `DEFAULT 0`).
  16 → 17 added the nullable `employee.suspendedAt` column (auto-migration).
