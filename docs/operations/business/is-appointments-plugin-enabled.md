[← Operations](../README.md)

# Is appointments plugin enabled

`IsAppointmentsPluginEnabled.flow(businessId)` / `.refresh(businessId)` → `GET /api/appointments/enabled/{businessId}`
· called from `DashboardViewModel`, `BusinessPluginsViewModel`, [Refresh business info](refresh-business-info.md),
[Get available dashboard features](get-available-dashboard-features.md) and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

A `Boolean?` stored in DataStore rather than Room. `null` means "never fetched" and is different from `false`.

```mermaid
flowchart TD
    F([flow businessId]) --> Obs[plugin_prefs.getFlow appointment_plugin_available_businessId] --> FR([emit Boolean?])

    R([refresh businessId]) --> Net[PluginDataSource.isAppointmentPluginAvailableOnRemote<br/>GET /api/appointments/enabled/businessId]
    Net --> Save[plugin_prefs := result]
    Save --> RR([return Boolean])
```
