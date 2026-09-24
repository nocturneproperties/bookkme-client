[← Operations](../README.md)

# Update employee

`UpdateEmployee(employee)` → `PUT /api/business/{businessId}/employee/{id}`
· **no view model calls it yet**

Network only. The server response is returned but **not written to `employee`**, so the cached list stays
stale until the next [Get employees](get-employees.md) refresh.

```mermaid
flowchart TD
    Start([invoke employee]) --> Net[EmployeeDataSource.updateEmployee<br/>PUT /api/business/businessId/employee/id]
    Net -- 2xx --> R([return Employee])
    Net -- business error --> Code{errorCode}
    Code -- 200021 EMPLOYEE_VALIDATION_ERROR --> E1([throw Error.ValidationError])
    Code -- 200022 EMPLOYEE_ACTIVE_DAY_WITHOUT_WORK_HOURS --> E2([throw Error.ActiveDayWithoutWorkHours])
    Code -- 200023 EMPLOYEE_INVALID_DAY_OFF_RANGE --> E3([throw Error.InvalidDayOffRange])
    Code -- other --> EX([rethrow])
```
