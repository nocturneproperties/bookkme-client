[← Operations](../README.md)

# Delete service group

`DeleteServiceGroup(group)` → `DELETE /api/business/{businessId}/service_group/{id}`
· called from `ServiceGroupListViewModel`

The local delete **cascades** to every cached `service` in the group (`service.groupId` has `ON DELETE CASCADE`).
Whatever the server does with the group's services, the next [Get services](get-services.md) refresh
reconciles the local list.

```mermaid
flowchart TD
    Start([invoke group]) --> Net[ServiceGroupDataSource.deleteServiceGroup businessId, id<br/>DELETE /api/business/businessId/service_group/id]
    Net -- 2xx --> Del[(deleteGroupFromDB group<br/>CASCADE → service rows in group)]
    Del --> R([Unit])
    Net -- error --> EX([rethrow])
```
