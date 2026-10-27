[← Operations](../README.md)

# Observe dashboard setup status

`ObserveDashboardSetupStatus()` → `Flow<DashboardSetupStatus>`, **local only**
· called from `DashboardViewModel`

Decides what the Home tab shows. It re-emits whenever the dashboard id, the business row or the plugin
preference changes. This includes right after [Create business](create-business.md) or
[Join business](join-business.md), because both switch the dashboard business.
The plugin flag counts as enabled only when it is exactly `true`. `null` ("not fetched yet") is treated as
disabled. `permissions.business.update` decides who is allowed to turn plugins on.

```mermaid
flowchart TD
    Start([invoke]) --> Biz[ObserveDashboardBusinessChanges]
    Biz -- null --> NB([emit NoBusiness])
    Biz -- business --> Plug[IsAppointmentsPluginEnabled.flow business.id<br/>plugin_prefs]
    Plug --> En{plugin == true}
    En -- yes --> R([emit Ready])
    En -- no / null --> Perm{permissions.business.update}
    Perm -- yes --> SR([emit SetupRequired business.id])
    Perm -- no --> AS([emit AwaitingSetup business.name])
```
