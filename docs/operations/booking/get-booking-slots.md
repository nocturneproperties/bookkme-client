[← Operations](../README.md)

# Get booking slots

`GetBookingSlots(businessId, timetable, from, to)` → appointments' [Get free slots](../appointments/get-free-slots.md)
(`POST /api/appointments/slots`) · called by `BookingDateTimeViewModel` for the week the step shows; bound by
`bookingDomainModule()`

Reads the services (with counts) and the specialist the client approved from the business's in-memory
[booking preparation](get-booking-preparation.md) and asks for their free slots on each date of the range. When no
specialist or no service is approved (the preparation was cleared), it throws `Error.SelectionInvalid` without asking
the backend, and `BookingDateTimeViewModel` leaves the booking flow. Each slot gets its local time in the time zone of
the [booking timetable](get-booking-timetable.md) the caller already observes, and each day is marked when the
timetable says the business is closed on it (an inactive weekday or a day off), so the step can strike closed days
through while fully booked days stay tappable. The business is not looked up again; the slots themselves always come
from the backend.

```mermaid
flowchart TD
    Start([invoke businessId, timetable, from, to]) --> Sel[BookingPreparationDataSource.observeSelection businessId .first]
    Sel --> Complete{specialist and at least one service approved?}
    Complete -- no --> E0([throw Error.SelectionInvalid])
    Complete -- yes --> Query[FreeSlotsQuery businessId, employeeId = selection.specialistId,<br/>services = selected serviceId + quantity, from, to]
    Query --> Free[GetFreeSlots query<br/>POST /api/appointments/slots]
    Free -- Error.AppointmentsNotEnabled --> E1([throw Error.BookingUnavailable])
    Free -- Error.InvalidServiceSelection 422 300024 --> E2([throw Error.SelectionInvalid])
    Free -- Error.InvalidRange 422 300033 --> E3([throw Error.InvalidRange])
    Free -- CancellationException or other error --> E4([rethrow unchanged])
    Free -- days --> Map[BookingDay date,<br/>slots: start + start.toLocalDateTime timetable.timeZone .time,<br/>isBusinessClosed: timetable.isClosedOn date]
    Map --> Done([return days])
```
