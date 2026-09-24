[← Operations](../README.md)

# Get services

`GetServices.flow()` / `.refresh(businessId)` → `GET /api/business/{businessId}/service`
· called from `ServiceListViewModel`, [Get appointment options](../appointments/get-appointment-options.md)
and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

The same list steps as [Get clients list](../clients/get-clients-list.md), with one addition: before the
services are upserted, the distinct groups embedded in the response are upserted into `service_group`, so the
`service.groupId` foreign key always holds.

```mermaid
flowchart TD
    F([flow]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> Empty([emit empty list])
    Biz -- business --> Obs[(serviceDao.observe business.id<br/>ServiceLocal with group)]
    Obs --> FR([emit sortedBy createdAt])

    R([refresh businessId]) --> Net[ServiceDataSource.getServices<br/>GET /api/business/businessId/service]
    Net --> Ids[(getServiceIdsInDb)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteServicesInDb staleIds)]
    Stale -- no --> Groups
    Del --> Groups[(ServiceGroupDataSource.saveGroupsInDB<br/>distinct service.group)]
    Groups --> Save[(saveServicesInDB)]
    Save --> Mark[services_prefs: last_synced_at_businessId]
    Mark --> RR([return sortedBy createdAt])
```
