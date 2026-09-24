[← Operations](../README.md)

# Cancel appointment

`CancelAppointment(appointmentId, businessId, reason)` → `POST /api/appointments/{id}/cancel`
· called from `AppointmentDetailsViewModel`

The local status update happens **inside the datasource's network method**
(`CommonAppointmentDataSource.cancelAppointment` calls `appointmentDao.updateStatus` after decoding the
response). This is the one exception to the rule that a datasource method either fetches or writes, never both.
Only `status` and `cancellationReason` are patched, and the service snapshot is untouched.

```mermaid
flowchart TD
    Start([invoke appointmentId, businessId, reason]) --> Build[AppointmentCancellation id, businessId, reason]
    Build --> Net[AppointmentDataSource.cancelAppointment<br/>POST /api/appointments/id/cancel]
    Net -- 2xx --> DB[(appointmentDao.updateStatus id, status, cancellationReason)]
    DB --> Emit[appointmentEvents.emit AppointmentEvent.Cancelled]
    Emit --> R([return Appointment])
    Net -- business error --> Code{errorCode}
    Code -- 300005 APPOINTMENT_ALREADY_CANCELED --> E1([throw Error.AppointmentAlreadyCancelled])
    Code -- 300006 APPOINTMENT_ALREADY_COMPLETED --> E2([throw Error.AppointmentAlreadyCompleted])
    Code -- other --> EX([rethrow])
```

**Emits:** `AppointmentEvent.Cancelled`. **Consumed by:** nobody at the moment. The list screen redraws
because it observes the `appointment` table.
