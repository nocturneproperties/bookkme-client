[← Operations](../README.md)

# Get upcoming bookings

`GetUpcomingBookings.flow()` / `.refresh()` → `GET /api/appointments/upcoming` + [`GetBusinesses`](../business/get-businesses.md)
· called from `ClientHomeViewModel` (client web app Home in dashboard presentation, "Upcoming") and, for `refresh` after a sent request, booking's [Send booking request](../booking/send-booking-request.md)

Client-facing: the route returns the caller's 2 nearest `SCHEDULED` appointments that have not ended (client
linked to the caller) and 2 nearest `PENDING` requests that have not started (made by the caller), across every
business, with the business's own `note` blanked. Both lists may be absent on the wire (empty repeated fields),
so `UpcomingBookingsRemote` defaults them to empty. The response carries business ids only, so `refresh()` reads
the booked businesses through `GetBusinesses.refresh(ids)` (one bulk request, saved into `business`) before saving
the bookings. A failed business read fails the refresh before the bookings are saved.

The bookings are cached in the shared `appointment` and `appointment_request` tables of `ClientDatabase`, which
on the web hold only the signed-in client's own bookings. Saving replaces the cached upcoming set: scheduled
appointments and pending requests the response no longer contains are deleted. `flow()` merges both tables and,
for their business ids, the cached businesses: appointments become `CONFIRMED`, requests
`WAITING_FOR_CONFIRMATION` (request dates converted to the device time zone, as appointment dates already are),
soonest first, named after their business (empty while it is not cached).

```mermaid
flowchart TD
    F([flow]) --> A[(appointmentDao.observeScheduled<br/>status = SCHEDULED, by date)]
    F --> Q[(appointmentRequestDao.observePending<br/>status = PENDING, by date)]
    A --> C[combine into UpcomingBookings]
    Q --> C
    C --> B[flatMapLatest GetBusinesses.flow businessIds<br/>businessDao.observeBusinesses]
    B --> M[appointments → CONFIRMED, requests → WAITING_FOR_CONFIRMATION,<br/>business name by id, sortBy date]
    M --> FR([emit List UpcomingBooking])

    R([refresh]) --> Net[ClientBookingDataSource.getUpcomingBookings<br/>GET /api/appointments/upcoming]
    Net --> Biz[GetBusinesses.refresh businessIds<br/>POST /api/business/bulk → upsert business]
    Biz -- error --> Err([throw, bookings not saved])
    Biz --> SaveB[(saveUpcomingBookingsInDb)]
    SaveB --> DA[(delete SCHEDULED appointments not in response<br/>upsertWithServices)]
    SaveB --> DQ[(delete PENDING requests not in response<br/>upsertWithServices)]
    DA --> RR([return merged List])
    DQ --> RR
```
