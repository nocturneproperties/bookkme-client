[← Operations](../README.md)

# Log out

`LogOut()` → calls `doOnLogOut()` on every Koin-bound `LogOutAction`
· called from `BootstrapViewModel.logOut()`, which the platform notification handlers invoke when any
screen posts `PresentationNotification.Unauthorized`. That happens on an `Error.Unauthorized`, which is an HTTP
401 that survived a token refresh, and on the Settings → Log out action.

Each action runs in its own `runCatching`, so one failure never blocks the rest. The actions run in Koin
registration order, which is not guaranteed. Clearing `authorization_prefs` flips `authorized` to false, and
`BootstrapViewModel` responds by navigating to Login.

```mermaid
flowchart TD
    Start([invoke]) --> Loop[for each LogOutAction]
    Loop --> A1[CommonAuthorizationDataSource<br/>authorization_prefs.clear + DELETE /api/auth/session]
    Loop --> A2[CommonUserProfileDataSource<br/>user_profile.clear]
    Loop --> A3[CommonBusinessDataSource<br/>business_prefs + business]
    Loop --> A4[CommonPluginDataSource<br/>plugin_prefs.clear]
    Loop --> A5[CommonClientsDataSource<br/>clients_prefs + client]
    Loop --> A6[ServiceDataSourceImpl / ServiceGroupDataSourceImpl<br/>prefs + service / service_group]
    Loop --> A7[EmployeeDataSourceImpl / EmployeeInvitationDataSourceImpl<br/>prefs + employee / employee_invitation]
    Loop --> A8[CommonAppointmentDataSource / CommonAppointmentRequestDataSource<br/>prefs + appointment / appointment_request]
    Loop --> A9[BiometryCore<br/>biometry_opt_in := null]
    Loop --> A10[CommonAppointmentSettingsDataSource<br/>appointment_settings]
    Loop --> A11[CommonNotificationSettingsDataSource<br/>notification_settings]
    A1 & A2 & A3 & A4 & A5 & A6 & A7 & A8 & A9 & A10 & A11 --> R([Unit, errors swallowed per action])
```

`authorization_prefs.clear()` runs **before** `DELETE /api/auth/session`. The sign-out request therefore
goes out with whatever token the Ktor bearer cache still holds. If that request fails, the server session
stays open.

Not cleared: the `notification_prefs`, `device_prefs` and `settings_prefs` buckets. See
[preferences](../../database/preferences.md).
