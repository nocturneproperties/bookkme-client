[← Operations](../README.md)

# Mark appointment as no-show

`MarkAppointmentNoShow(appointmentId)` → `POST /api/appointments/{id}/no-show`
· called from `AppointmentDetailsViewModel` (the "Client didn't show up" button, after a confirmation dialog)

Marks a **started** `SCHEDULED` or `COMPLETED` appointment as `NO_SHOW`. The backend treats an
appointment that is already `NO_SHOW` as a no-op. No request body.

```mermaid
flowchart TD
    Start([invoke appointmentId]) --> Net[AppointmentDataSource.markAppointmentNoShow<br/>POST /api/appointments/id/no-show]
    Net -- 2xx result --> Save[(saveAppointmentInDB result<br/>upsertWithServices)]
    Save --> R([return result])
    Net -- business error --> Code{errorCode}
    Code -- 300005 APPOINTMENT_ALREADY_CANCELED --> E1([throw Error.AppointmentAlreadyCancelled])
    Code -- 300018 APPOINTMENT_NOT_STARTED --> E2([throw Error.AppointmentNotStarted])
    Code -- other --> EX([rethrow])
```

The details screen shows the button only when `Appointment.canBeMarkedNoShow()` is true (`SCHEDULED` or
`COMPLETED`, and `date <= now`), which mirrors the backend's `requireNoShowMarkable` guard.
