[← Operations](../README.md)

# Update appointment (reschedule)

`UpdateAppointment(appointment)` → `PUT /api/appointments/{id}`
· called from `AppointmentDetailsViewModel`

The server response is saved to the database and sent with the event, but the use case **returns the
input `appointment`** and not the server result. A caller that uses the return value sees the values it
submitted, not the server-normalized ones. The database and the event carry the server's version.

```mermaid
flowchart TD
    Start([invoke appointment]) --> Net[AppointmentDataSource.updateAppointment<br/>PUT /api/appointments/id]
    Net -- 2xx result --> Save[(saveAppointmentInDB result<br/>upsertWithServices)]
    Save --> Emit[appointmentEvents.emit AppointmentEvent.Updated result]
    Emit --> R([return input appointment])
    Net -- business error --> Code{errorCode}
    Code -- 300003 DATE_NOT_ALLOWED --> E1([throw Error.DateIsNotAllowed])
    Code -- 300002 TIME_NOT_ALLOWED --> E2([throw Error.TimeIsNotAllowed])
    Code -- 300004 APPOINTMENT_EXISTS --> E3([throw Error.AppointmentOverlap])
    Code -- other --> EX([rethrow])
```

**Emits:** `AppointmentEvent.Updated`. **Consumed by:** `AppointmentListViewModel.reload()`, which refreshes the currently
selected date and the request count.
