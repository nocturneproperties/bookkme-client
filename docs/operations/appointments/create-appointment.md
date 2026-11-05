[← Operations](../README.md)

# Create appointment with a fresh quote

`CreateAppointment(draft)` → quote and validation → `POST /api/appointments/instant`.
The caller supplies displayed service snapshots and may select `employeeId`; when omitted, the cached
business's caller employee id is used. No client-generated appointment id or initiator snapshot is sent.

```mermaid
flowchart TD
    Start([invoke draft]) --> Empty{services empty?}
    Empty -- yes --> EmptyError([throw Error.EmptyServices])
    Empty -- no --> Employee{draft.employeeId supplied?}
    Employee -- yes --> Selected[use selected employeeId]
    Employee -- no --> Biz[ObserveUserBusinessesChanges.first<br/>find business employeeId]
    Biz -- missing --> NotEmployee([throw Error.NotAnEmployee])
    Biz -- employeeId --> Selected
    Selected --> Quote[CreateQuote businessId, distinct service ids<br/>POST /api/business/businessId/service/quote]
    Quote -- ServiceNotFound --> Refresh
    Quote -- result --> Match{service ids, business, availability,<br/>name, group, price, duration match?}
    Match -- no --> Refresh[GetServices.refresh businessId<br/>GET services and update Room service cache]
    Refresh --> Changed([throw PriceChanged, DurationChanged,<br/>ServicesChanged or ServiceNotFound])
    Refresh -- failure --> RefreshError([propagate refresh failure])
    Match -- yes --> Build[InstantAppointmentDraft<br/>businessId, employeeId, RequestedService ids/counts,<br/>date, trimmed note, fresh offerToken, clientId]
    Build --> Net[AppointmentDataSource.createAppointment<br/>POST /api/appointments/instant]
    Net -- success --> Save[(saveAppointmentInDB returned appointment<br/>upsertWithServices)] --> R([return server appointment])
    Net -- business error --> Code{errorCode}
    Code -- 300013 / 300017 / 300014 / 200013 --> Refresh
    Code -- 300004 --> Overlap([throw Error.AppointmentOverlap])
    Code -- 300002 --> Time([throw Error.TimeIsNotAllowed])
    Code -- 300003 --> Date([throw Error.DateIsNotAllowed])
    Code -- 300012 --> Past([throw Error.DateInPast])
    Code -- 300016 --> Used([throw Error.OfferAlreadyUsed])
    Code -- 200014 --> EmptyError
    Code -- 200024 --> EmployeeMissing([throw Error.EmployeeNotFound])
    Code -- 200006 --> ClientMissing([throw Error.ClientNotFound])
    Code -- 200033 --> Suspended([throw Error.EmployeeSuspended])
    Code -- other --> Other([propagate])
```

Duplicate service selections become positive counts; the quote requests each id once. Quote mismatches
and quote-related booking races refresh backend services before returning the mismatch error. If that
refresh fails, its error propagates and booking is not retried. A refreshed quote never silently accepts
changed booking details. A fresh request from the caller is required after reviewing updates.

The creation screen observes the local service cache through `ObserveAppointmentOptions`. Each
emission replaces selected service snapshots by id, removes selections missing from the options,
and recalculates the subtotal and Create availability. The client, date, time and note are preserved.
Option changes keep Create disabled while a booking is pending. Quote rejection explains that the
employee must review the updated form; the ViewModel never automatically retries the booking.

Composes `CreateQuote`, `GetServices.refresh` on mismatch, and `ObserveUserBusinessesChanges` for the
default employee. Quote retrieval and cache refresh are use-case orchestration, never combined in a datasource.
