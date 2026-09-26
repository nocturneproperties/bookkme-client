[← Operations](../README.md)

# Get clients list

`GetClientsList.flow()` / `.refresh(businessId)` → `GET /api/business/{businessId}/clients`
· called from `ClientsListViewModel`, [Get appointment options](../appointments/get-appointment-options.md)
and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

This is the reference shape for every cached list in the app (services, service groups, employees,
invitations, appointment requests and per-day appointments all do the same thing).

```mermaid
flowchart TD
    F([flow]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> Empty([emit empty list])
    Biz -- business --> Obs[(clientsDao.observeClients business.id)]
    Obs --> FR([emit List Client])

    R([refresh businessId]) --> Net[ClientsDataSource.getClients<br/>GET /api/business/businessId/clients]
    Net --> Ids[(getClientIdsInDb businessId)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteClientsInDb staleIds, chunked)]
    Stale -- no --> Save
    Del --> Save[(saveClientsInDb clients, upsert)]
    Save --> Mark[clients_prefs: last_synced_at_businessId := now]
    Mark --> RR([return List Client])
```
