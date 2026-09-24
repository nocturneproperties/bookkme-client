[← Operations](../README.md)

# Get available dashboard features

`GetAvailableDashboardFeatures()` → `Flow<DashboardOverview?>`, **local only**
· called from `BusinessDashboardViewModel`

Derives the set of dashboard tiles from the selected business's permission columns and the cached plugin
flag. It re-emits whenever the dashboard id, the business row, or the plugin preference changes.
`APPOINTMENTS` needs the plugin flag to be exactly `true`. `null` ("not fetched yet") hides the tile.

```mermaid
flowchart TD
    Start([invoke]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> N([emit null])
    Biz -- business --> Plug[IsAppointmentsPluginEnabled.flow business.id<br/>plugin_prefs]
    Plug --> Build[features = empty set]
    Build --> P1{permissions.business.view} -- yes --> F1[+ BUSINESS]
    Build --> P2{permissions.employees.view} -- yes --> F2[+ EMPLOYEES]
    Build --> P3{permissions.clients.view} -- yes --> F3[+ CLIENTS]
    Build --> P4{permissions.services.view} -- yes --> F4[+ SERVICES]
    Build --> P5{plugin == true AND permissions.appointments.view} -- yes --> F5[+ APPOINTMENTS]
    F1 & F2 & F3 & F4 & F5 --> R([emit DashboardOverview business, features])
```
