[← Operations](../README.md)

# Delete service

`DeleteService(service)` → `DELETE /api/business/{businessId}/service/{id}`
· called from `ServiceListViewModel`

```mermaid
flowchart TD
    Start([invoke service]) --> Net[ServiceDataSource.deleteService businessId, id<br/>DELETE /api/business/businessId/service/id]
    Net -- 2xx --> Del[(deleteServiceFromDB service)]
    Del --> R([Unit])
    Net -- error --> EX([rethrow])
```

Snapshots of the service in `appointment_service_snapshot` and `employee_service_snapshot` are kept, because
they have no foreign key to `service`.
