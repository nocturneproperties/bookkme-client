[← Operations](../README.md)

# Get employees

`GetEmployees.flow()` / `.refresh(businessId)` → `GET /api/business/{businessId}/employee`
· called from `EmployeeListViewModel` and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

The same list steps as [Get clients list](../clients/get-clients-list.md). Each employee is saved as an
aggregate: `upsertAllWithChildren` replaces the day schedules, work hours, days off and service snapshots of
each employee in the response.

```mermaid
flowchart TD
    F([flow]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> Empty([emit empty list])
    Biz -- business --> Obs[(employeeDao.observeEmployees business.id<br/>EmployeeLocal)]
    Obs --> FR([emit List Employee])

    R([refresh businessId]) --> Net[EmployeeDataSource.getEmployees<br/>GET /api/business/businessId/employee]
    Net --> Ids[(getEmployeeIdsInDb)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteEmployeesInDb staleIds<br/>CASCADE → schedules, snapshots)]
    Stale -- no --> Save
    Del --> Save[(saveEmployeesInDb<br/>upsertAllWithChildren)]
    Save --> Mark[employees_prefs: last_synced_at_businessId]
    Mark --> RR([return List Employee])
```
