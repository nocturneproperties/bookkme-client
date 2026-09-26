[← Operations](../README.md)

# Get service groups

`GetServiceGroups.flow()` / `.refresh(businessId)` → `GET /api/business/{businessId}/service_group`
· called from `ServiceGroupListViewModel`, `AddServiceViewModel` and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

The same list steps as [Get clients list](../clients/get-clients-list.md). Both paths sort by `createdAt`.
Deleting stale groups locally cascades to their cached services.

```mermaid
flowchart TD
    F([flow]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> Empty([emit empty list])
    Biz -- business --> Obs[(serviceGroupDao.observe business.id)]
    Obs --> FR([emit sortedBy createdAt])

    R([refresh businessId]) --> Net[ServiceGroupDataSource.getServiceGroups<br/>GET /api/business/businessId/service_group]
    Net --> Ids[(getServiceGroupIdsInDb)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteGroupsInDB staleIds<br/>CASCADE → services)]
    Stale -- no --> Save
    Del --> Save[(saveGroupsInDB)]
    Save --> Mark[service_groups_prefs: last_synced_at_businessId]
    Mark --> RR([return sortedBy createdAt])
```
