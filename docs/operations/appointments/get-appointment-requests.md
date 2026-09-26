[← Operations](../README.md)

# Get appointment requests

`GetAppointmentRequests.flow(businessId)` / `.refresh(businessId)` → `GET /api/appointments/request/{businessId}`
· called from `AppointmentListViewModel` and `AppointmentRequestViewModel`

Requests are cached in **every** status, but both paths return only `PENDING` requests, sorted by `date`.

```mermaid
flowchart TD
    F([flow businessId]) --> Obs[(appointmentRequestDao.observeForBusiness)]
    Obs --> Filt1[filter PENDING, sortBy date] --> FR([emit List])

    R([refresh businessId]) --> Net[AppointmentRequestDataSource.getAppointmentRequests<br/>GET /api/appointments/request/businessId]
    Net --> Ids[(getAppointmentRequestIdsInDb businessId)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteAppointmentRequestsInDb staleIds<br/>chunked, cascades snapshots)]
    Stale -- no --> Save
    Del --> Save[(saveAppointmentRequestsInDB<br/>upsertWithServices)]
    Save --> Mark[saveLastSyncedAt businessId]
    Mark --> Filt2[filter PENDING, sortBy date] --> RR([return List])
```
