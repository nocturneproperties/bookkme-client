[← Operations](../README.md)

# Approve appointment request

`ApproveAppointmentRequest(requestId)` → `POST /api/appointments` (body `AppointmentRequestIdRemote`)
· called from `AppointmentRequestViewModel`

Turns a pending request into a real appointment on the backend. The new appointment is **not** written to the
local database here. The use case emits `AppointmentEvent.Created`, and `AppointmentListViewModel` reacts by
refreshing the day. `AppointmentRequestViewModel` drops the item from its in-memory list on success. The cached request row
stays `PENDING` until the next [Get appointment requests](get-appointment-requests.md) `refresh()`.
`AppointmentListViewModel.reload()` runs one when it reloads the request count.

```mermaid
flowchart TD
    Start([invoke requestId]) --> Net[AppointmentRequestDataSource.createAppointmentFromRequest<br/>POST /api/appointments]
    Net -- 2xx --> Emit[appointmentEvents.emit AppointmentEvent.Created]
    Emit --> R([return Appointment])
    Net -- business error --> Code{errorCode}
    Code -- 300004 APPOINTMENT_EXISTS --> E1([throw Error.AppointmentExists])
    Code -- 300003 DATE_NOT_ALLOWED --> E2([throw Error.DateNotAllowed])
    Code -- 300002 TIME_NOT_ALLOWED --> E3([throw Error.TimeNotAllowed])
    Code -- 300012 DATE_IN_PAST --> E4([throw Error.DateInPast])
    Code -- other --> EX([rethrow])
    Net -- network / other --> EX
```

**Emits:** `AppointmentEvent.Created`. **Consumed by:** `AppointmentListViewModel.reload()`, which calls
[Get appointments for business](get-appointments-for-business.md) `refresh()` for the selected date and
[Get appointment requests](get-appointment-requests.md) `refresh()`.
