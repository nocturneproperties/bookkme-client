[← Operations](../README.md)

# User profile CRUD

`UserProfileCRUD` → `GET /api/user/me` / `PATCH /api/user/me`
· used by [Initial app data fetch](initial-app-data-fetch.md), [Create appointment](../appointments/create-appointment.md),
[Get settings](../settings/get-settings.md), [Edit profile](../settings/edit-profile.md) and
[Get notification settings](../settings/get-notification-settings.md)

```mermaid
flowchart TD
    U([updateFromRemote]) --> UG[GET /api/user/me] --> UU[(upsert user_profile)] --> UR([Unit])

    G([get]) --> GD[(profileDao.queryProfile)]
    GD -- row --> GR([return cached])
    GD -- null --> GN[GET /api/user/me] --> GU[(upsert)] --> GR2([return fetched])

    O([observe]) --> OD[(profileDao.observeProfile)] --> OR([emit UserProfile?])

    P([update profile]) --> P1[updateProfile profile<br/>PATCH /api/user/me]
    P1 --> P2[GET /api/user/me]
    P2 --> P3[(upsertProfile serverProfile)]
    P3 --> PR([Unit])

    D([delete id]) --> DD[(profileDao.deleteById)] --> DR([Unit])
```

`update` sends the edit once, then caches the server's copy of the profile (so server-side normalization
is what the app shows). `UserProfileDataSource.updateProfile` is network-only.
