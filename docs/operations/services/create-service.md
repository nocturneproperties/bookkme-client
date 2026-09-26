[← Operations](../README.md)

# Create service

`CreateService(service)` → `POST /api/business/{businessId}/service`
· called from `AddServiceViewModel`

```mermaid
flowchart TD
    Start([invoke service]) --> Net[ServiceDataSource.createService<br/>POST /api/business/businessId/service]
    Net -- 2xx created --> Save[(saveServiceInDB created, upsert)]
    Save --> R([return created Service])
    Net -- error --> EX([rethrow, no domain mapping])
```

The local upsert assumes the service's group is already in `service_group`. If it is not, the
`service.groupId` foreign key fails and the create **looks failed even though it succeeded on the server**.
`AddServiceViewModel` only offers groups from [Get service groups](get-service-groups.md), which were cached.
