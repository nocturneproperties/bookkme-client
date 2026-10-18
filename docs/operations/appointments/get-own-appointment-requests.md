[← Operations](../README.md)

# Get own appointment requests

`GetOwnAppointmentRequests.flow(businessId)` / `.refresh(businessId)` → `GET /api/appointments/request/{businessId}/mine`
· called from `AppointmentListViewModel` (request count) and `AppointmentRequestViewModel` (requests list)

The route returns only the caller's own `PENDING` requests in the business and needs no appointments `view`
permission. The business-wide `GET /api/appointments/request/{businessId}` is not used by the app, so
`appointment_request` only ever holds the signed-in user's requests and the stale-row cleanup below cannot
remove anything else. Both paths still filter to `PENDING` and sort by `date`, so a row whose status changed
locally drops out at once.

```mermaid
flowchart TD
    F([flow businessId]) --> Obs[(appointmentRequestDao.observeForBusiness)]
    Obs --> Filt1[filter PENDING, sortBy date] --> FR([emit List])

    R([refresh businessId]) --> Net[AppointmentRequestDataSource.getOwnAppointmentRequests<br/>GET /api/appointments/request/businessId/mine]
    Net --> Ids[(getAppointmentRequestIdsInDb businessId)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteAppointmentRequestsInDb staleIds<br/>chunked, cascades snapshots)]
    Stale -- no --> Save
    Del --> Save[(saveAppointmentRequestsInDB<br/>upsertWithServices)]
    Save --> Mark[saveLastSyncedAt businessId]
    Mark --> Filt2[filter PENDING, sortBy date] --> RR([return List])
```
