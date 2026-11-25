[← Operations](../README.md)

# Get client appointment

`GetClientAppointment.flow(appointmentId)` → local only: Room `appointment` by id (any status) + [Get
businesses](../business/get-businesses.md) `flow`
· called by booking's [Get booking appointment details](../booking/get-booking-appointment-details.md); bound by
`clientAppointmentsDomainModule()` (client web app only)

Reads one of the client's own appointments by id, whatever its status, so the appointment details screen works for
every list that caches appointments (today the upcoming bookings; later the client's booking history). It is named
after its business through the cached `business` row (empty while that is not cached) and carries the client's note in
`clientNote`. It emits `null` while the appointment is not cached. There is no `refresh()`: the backend has no
client-readable route for one appointment (`GET /api/appointments/{id}` needs the business's view grant), so the
lists that fetch appointments keep the rows current.

```mermaid
flowchart TD
    Start([flow appointmentId]) --> Dao[(ClientBookingDataSource.observeAppointment<br/>appointmentDao.observeById)]
    Dao --> Cached{row cached?}
    Cached -- no --> Null([emit null])
    Cached -- yes --> Biz[GetBusinesses.flow setOf businessId<br/>businessDao.observeBusinesses]
    Biz --> Emit([emit ClientAppointment appointment, business name or empty])
```
