[← Operations](../README.md)

# Set employee permission

`SetEmployeePermission(businessId, id, resource, permission)` →
`PUT /api/business/{businessId}/employee/{id}/permissions/{resource}` · **no view model calls it yet**

`resource` goes on the wire as `BusinessResource.name.lowercase()` (`business`, `employees`, `clients`,
`services`, `appointments`). The response is the employee's full updated grant set. Nothing is cached.

```mermaid
flowchart TD
    Start([invoke businessId, id, resource, permission]) --> Net[EmployeeDataSource.setEmployeePermission<br/>PUT .../employee/id/permissions/resource]
    Net -- 2xx --> R([return BusinessPermissions])
    Net -- business error --> Code{errorCode}
    Code -- 200027 INSUFFICIENT_GRANT_PERMISSION --> E1([throw Error.InsufficientGrant])
    Code -- other --> EX([rethrow])
```
