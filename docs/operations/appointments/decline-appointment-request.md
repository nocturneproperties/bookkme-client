[← Operations](../README.md)

# Decline appointment request

`DeclineAppointmentRequest(requestId, businessId, reason)` → `POST /api/appointments/request/{id}/decline`
· called from `AppointmentRequestViewModel`

Network only. It neither writes to the database nor emits an event. On success, `AppointmentRequestViewModel`
removes the row from its in-memory list state. The cached `appointment_request` row keeps its old `PENDING`
status until the next [Get appointment requests](get-appointment-requests.md) `refresh()`. Until then, any
`flow()` subscriber still sees it as pending.

```mermaid
flowchart TD
    Start([invoke requestId, businessId, reason]) --> Net[AppointmentRequestDataSource.declineAppointmentRequest<br/>POST /api/appointments/request/id/decline]
    Net -- 2xx --> R([Unit])
    Net -- business error --> Code{errorCode}
    Code -- 300007 REQUEST_ALREADY_DECLINED --> E1([throw Error.AlreadyDeclined])
    Code -- 300008 REQUEST_ALREADY_APPROVED --> E2([throw Error.AlreadyApproved])
    Code -- other --> EX([rethrow])
```
