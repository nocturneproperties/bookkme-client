[← Operations](../README.md)

# Edit profile

`EditProfile(firstName, lastName)` → `PATCH /api/user/me`
· called from `EditProfileViewModel`

First and last name are trimmed before they are saved. The email is sent back unchanged from the
stored profile: since backend 0.18.0 a different email is rejected with 100006 and must go through
the email change flow instead ([Start email change](../authorization/start-email-change.md) →
[Confirm email change](../authorization/confirm-email-change.md), opened from Edit Profile's "Change email").

```mermaid
flowchart TD
    Start([invoke firstName, lastName]) --> Get[UserProfileCRUD.get<br/>DB first, else GET /api/user/me]
    Get --> Copy[profile.copy firstName, lastName, each trimmed<br/>email kept as stored]
    Copy --> Upd[UserProfileCRUD.update<br/>PATCH, GET, upsert user_profile]
    Upd --> R([Unit])
    Upd -- 422 100002 USER_CONTACT_TAKEN --> Taken([throw Error.ContactAlreadyTaken])
    Upd -- 422 100006 EMAIL_CHANGE_REQUIRES_VERIFICATION --> Verify([throw Error.EmailChangeRequiresVerification])
```

See [User profile CRUD](../authorization/user-profile-crud.md) for the update steps.
