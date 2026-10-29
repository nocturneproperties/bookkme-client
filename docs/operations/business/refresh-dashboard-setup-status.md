[← Operations](../README.md)

# Refresh dashboard setup status

`RefreshDashboardSetupStatus(businessId)` → `GET /api/appointments/enabled/{id}`, plus the service group,
service and client list fetches of the composed use cases
· called from `DashboardViewModel`

Re-fetches everything [Observe dashboard setup status](observe-dashboard-setup-status.md) reads,
so the onboarding checklist reflects the server and not a stale or never-synced cache. Sign-in already
refreshes these for the dashboard business through [Initial app data fetch](../authorization/initial-app-data-fetch.md),
but switching to another business does not. `DashboardViewModel` calls it in two ways:

- **Automatically**, silently: once each time the dashboard business id it sees in `SetupRequired` or
  `AwaitingSetup` changes (a separate subscription to the setup status, `distinctUntilChanged` on the id).
  Failures are ignored.
- **On pull to refresh** on the Home onboarding screens, after [Refresh business info](refresh-business-info.md),
  so a user waiting on Setup required or Awaiting setup can pick up changes made by someone else. Failures are
  shown.

Each refresh runs even when an earlier one failed. After all four have run, the first failure is rethrown.

```mermaid
flowchart TD
    Start([invoke businessId]) --> Plug[runCatching IsAppointmentsPluginEnabled.refresh businessId<br/>GET /api/appointments/enabled/id<br/>plugin_prefs]
    Plug --> Groups[runCatching GetServiceGroups.refresh businessId<br/>service_group]
    Groups --> Services[runCatching GetServices.refresh businessId<br/>service, service_group]
    Services --> Clients[runCatching GetClientsList.refresh businessId<br/>client]
    Clients --> Failed{any failure?}
    Failed -- no --> R([Unit])
    Failed -- yes --> EX([rethrow first failure])
```
