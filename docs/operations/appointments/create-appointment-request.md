[← Operations](../README.md)

# Create appointment request

`CreateAppointmentRequest(draft)` → `POST /api/appointments/request`
· not called from any screen yet (no client-side booking flow exists)

A client asks a business for a slot. The `AppointmentRequestDraft` carries only ids and the slot:
`businessId` (#1), `employeeId` (#2), `services` (#3) as `RequestedService(serviceId, count)`, `date` (#4),
`note` (#5) and `offerToken` (#6). The token is the signed quote that `CreateQuote`
(`POST /api/business/{businessId}/service/quote`) returns, and it freezes the price and duration the client
saw. The backend resolves employee, client and services itself and attaches the request to the caller.

The backend rejects the same `serviceId` appearing in two entries, so the use case first merges such entries
into a single count, in first-booked order. The response is `204` with no body. Nothing is written locally,
since the request belongs to the business's request list and not the client's.

```mermaid
flowchart TD
    Start([invoke draft]) --> Merge[merge services with the same serviceId<br/>into one RequestedService, counts summed]
    Merge --> Net[AppointmentRequestDataSource.createAppointmentRequest<br/>POST /api/appointments/request body AppointmentRequestDraft]
    Net -- 204 --> R([return Unit])
    Net -- business error --> Code{errorCode}
    Code -- 300001 REQUEST_EXISTS --> E1([throw Error.RequestForThisTimeExists])
    Code -- 300002 TIME_NOT_ALLOWED --> E2([throw Error.TimeIsNotAllowed])
    Code -- 300003 DATE_NOT_ALLOWED --> E3([throw Error.DateIsNotAllowed])
    Code -- 300012 DATE_IN_PAST --> E4([throw Error.DateInPast])
    Code -- 300013 PRICE_CHANGED --> E5([throw Error.PriceChanged])
    Code -- 300017 DURATION_CHANGED --> E6([throw Error.DurationChanged])
    Code -- 300014 SERVICES_VALIDATION_FAILED --> E7([throw Error.ServicesDoNotMatchOffer])
    Code -- 300016 QUOTE_TOKEN_ALREADY_USED --> E8([throw Error.OfferAlreadyUsed])
    Code -- 200013 BUSINESS_QUOTE_SERVICE_NOT_FOUND --> E9([throw Error.ServiceNotFound])
    Code -- 200024 BUSINESS_EMPLOYEE_NOT_EXISTS --> E10([throw Error.EmployeeNotFound])
    Code -- 200033 BUSINESS_EMPLOYEE_SUSPENDED --> E11([throw Error.EmployeeSuspended])
    Code -- other --> EX([rethrow])
```

`PriceChanged`, `DurationChanged` and `OfferAlreadyUsed` mean the quote is stale or spent: request a new
quote and resubmit. The backend releases the token again on every other failure, so retrying with the same
token after fixing the slot is fine.
