[← Operations](../README.md)

# Get notification settings

`GetNotificationSettings.flow()` / `.refresh()` → `GET /api/notifications/settings`
· called from `NotificationSettingsViewModel` and [low-priority data fetch](../authorization/initial-app-data-fetch.md)

`flow` is keyed on the cached profile's id. It emits `null` while there is no profile row. `refresh` calls
`UserProfileCRUD.get()` first, which fetches and caches the profile if it is missing. That wakes up `flow` as
a side effect.

```mermaid
flowchart TD
    F([flow]) --> P[UserProfileCRUD.observe]
    P -- null --> N([emit null])
    P -- profile --> Obs[(notificationSettingsDao.observeByUserId profile.id)] --> FR([emit NotificationSettings?])

    R([refresh]) --> Get[UserProfileCRUD.get<br/>ensures user_profile row]
    Get --> Net[NotificationSettingsDataSource.getNotificationSettings<br/>GET /api/notifications/settings]
    Net --> Save[(saveNotificationSettingsInDB<br/>upsertWithChildren: settings + channels)]
    Save --> RR([return NotificationSettings])
```

Decide whether a channel is shown by its **presence** in `channels`. Do not use `availableToClients`, which is
always `true`.
