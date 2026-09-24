[← Operations](../README.md)

# Promote employee

`PromoteEmployee(businessId, id, role)` → `POST /api/business/{businessId}/employee/{id}/promote`
· **no view model calls it yet**

Network only, with no error mapping and no local write.

```mermaid
flowchart TD
    Start([invoke businessId, id, role]) --> Net[EmployeeDataSource.promoteEmployee<br/>POST /api/business/businessId/employee/id/promote]
    Net -- 2xx --> R([Unit])
    Net -- error --> EX([rethrow])
```
