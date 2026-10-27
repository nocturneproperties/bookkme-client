[← Operations](../README.md)

# Update notification token

`UpdateNotificationToken(token)` / `UpdateNotificationToken()` → `PUT /api/notifications/{deviceUuid}/token`
· `invoke(token)` is called from `library/notifications` `TokenBridge.updateInstallationId` when the platform
issues a push token. `invoke()` is called from [low-priority data fetch](../authorization/initial-app-data-fetch.md).

A token that fails to upload is saved as `notification_prefs.pending_notification_token` and retried by the
no-argument overload on the next low-priority fetch. A successful upload clears it.

```mermaid
flowchart TD
    A([invoke token]) --> Dev[DeviceDataSource.getOrCreateDeviceUUID]
    Dev --> Net[NotificationSettingsDataSource.updateNotificationToken<br/>PUT /api/notifications/deviceUuid/token]
    Net -- 2xx --> Clr[notification_prefs: pending_notification_token := null] --> R([Unit])
    Net -- error --> Keep[notification_prefs: pending_notification_token := token] --> EX([rethrow])

    B([invoke]) --> Pend[notification_prefs.pending_notification_token]
    Pend -- null --> Noop([Unit])
    Pend -- token --> A
```
