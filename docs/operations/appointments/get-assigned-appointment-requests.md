[← Operations](../README.md)

# Get assigned appointment requests

`GetAssignedAppointmentRequests.flow(businessId)` / `.refresh(businessId)` → `GET /api/appointments/request/{businessId}/assigned`
· called from `AppointmentListViewModel` (request count) and `AppointmentRequestViewModel` (requests list)

The route returns the `PENDING` requests in the business that the caller, as an employee, is booked to serve,
earliest first. It needs the appointments `view` permission and answers 404 without it, which reaches the
callers as a mapped error (`AppointmentListViewModel` ignores it, `AppointmentRequestViewModel` shows it). The
business-wide `GET /api/appointments/request/{businessId}` is not used by the app, so `appointment_request`
only ever holds requests assigned to the signed-in user and the stale-row cleanup below cannot remove anything
else. Both paths still filter to `PENDING` and sort by `date`, so a row whose status changed
locally drops out at once.

```mermaid
flowchart TD
    F([flow businessId]) --> Obs[(appointmentRequestDao.observeForBusiness)]
    Obs --> Filt1[filter PENDING, sortBy date] --> FR([emit List])

    R([refresh businessId]) --> Net[AppointmentRequestDataSource.getAssignedAppointmentRequests<br/>GET /api/appointments/request/businessId/assigned]
    Net --> Ids[(getAppointmentRequestIdsInDb businessId)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteAppointmentRequestsInDb staleIds<br/>chunked, cascades snapshots)]
    Stale -- no --> Save
    Del --> Save[(saveAppointmentRequestsInDB<br/>upsertWithServices)]
    Save --> Mark[saveLastSyncedAt businessId]
    Mark --> Filt2[filter PENDING, sortBy date] --> RR([return List])
```
