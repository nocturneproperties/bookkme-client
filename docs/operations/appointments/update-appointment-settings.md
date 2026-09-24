[← Operations](../README.md)

# Update appointment settings

`UpdateAppointmentSettings(settings)` → `PUT /api/appointments/settings/{businessId}`
· called from `AppointmentSettingsViewModel`

Server-first: the database is only written with the server's response, never with the submitted value.

```mermaid
flowchart TD
    Start([invoke settings]) --> Net[AppointmentSettingsDataSource.updateAppointmentSettings<br/>PUT /api/appointments/settings/businessId]
    Net -- 2xx result --> Save[(saveAppointmentSettingsInDB result<br/>upsertWithChildren)]
    Save --> R([return result])
    Net -- error --> EX([rethrow, no domain mapping])
```
