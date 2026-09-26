[← Operations](../README.md)

# Observe user businesses

`ObserveUserBusinessesChanges()` → `Flow<List<Business>>`, **local only** (`businessDao.observeAllBusinesses`)
· called from `BusinessDashboardViewModel` (business switcher) and `DashboardViewModel` (business list on the
Get started screen)

Emits every row in `business`. Logout clears the table, so rows from a previous account never appear.
[Refresh business info](refresh-business-info.md) deletes businesses missing from the server's response, so
a business the user has left drops out of the list on the next refresh.

```mermaid
flowchart TD
    Start([invoke]) --> Obs[(businessDao.observeAllBusinesses<br/>with day schedules, work hours, day offs)]
    Obs --> R([emit List Business])
```
