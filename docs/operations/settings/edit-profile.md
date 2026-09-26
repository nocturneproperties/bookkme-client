[← Operations](../README.md)

# Edit profile

`EditProfile(firstName, lastName, email)` → `PATCH /api/user/me`
· called from `EditProfileViewModel` and `SettingsDashboardViewModel`

```mermaid
flowchart TD
    Start([invoke firstName, lastName, email]) --> Get[UserProfileCRUD.get<br/>DB first, else GET /api/user/me]
    Get --> Copy[profile.copy firstName, lastName, email]
    Copy --> Upd[UserProfileCRUD.update<br/>PATCH, GET, upsert user_profile]
    Upd --> R([Unit])
```

See [User profile CRUD](../authorization/user-profile-crud.md) for the update steps.
