[← Operations](../README.md)

# Get client appointment request

`GetClientAppointmentRequest.flow(requestId)` → local only: Room `appointment_request` by id (any status)
· called by booking's [Get booking request details](../booking/get-booking-request-details.md); bound by
`clientAppointmentsDomainModule()` (client web app only)

Reads one of the client's own requests by id, whatever its status, so the request details screen works for every
list that caches requests (Home's upcoming bookings and the Bookings screen). It carries its business's name in
`businessName` (sent by the backend with every request, stored in `appointment_request.businessName`). It emits `null` while the request is
not cached, for example after an upcoming bookings refresh deleted a request the business answered meanwhile;
[Withdraw appointment request](withdraw-appointment-request.md) keeps the row and sets it to `CANCELLED`. There is
no `refresh()`: the backend has no client-readable route for one request (`GET /api/appointments/{id}` needs the
business's view grant), so the lists that fetch requests keep the rows current.

```mermaid
flowchart TD
    Start([flow requestId]) --> Dao[(ClientBookingDataSource.observeRequest<br/>appointmentRequestDao.observeById)]
    Dao --> Cached{row cached?}
    Cached -- no --> Null([emit null])
    Cached -- yes --> Emit([emit AppointmentRequest with businessName])
```
