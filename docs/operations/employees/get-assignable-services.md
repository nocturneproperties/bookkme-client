[← Operations](../README.md)

# Get assignable services

`GetAssignableServices.flow()` / `.refresh(businessId)` → delegates to [Get services](../services/get-services.md)
· called from `EditEmployeeViewModel`

A wrapper in the employees domain so `feature/employees/presentation` does not depend on the services domain.
It returns the business services unchanged. They are the options of the services picker on the edit employee
screen.

```mermaid
flowchart TD
    F([flow]) --> GF[GetServices.flow]
    GF --> FR([emit List Service])

    R([refresh businessId]) --> GR[GetServices.refresh businessId<br/>GET /api/business/businessId/service]
    GR --> RR([return List Service])
```
