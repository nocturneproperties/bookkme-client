[← Operations](../README.md)

# Get booking preparation

`GetBookingPreparation.flow(businessId)` → local only (`BookingPreparationDataSource`, in memory) and
[Get business services](../services/get-business-services.md) `flow` · called from `BookingServicesViewModel`;
bound by `bookingDomainModule()`

The booking a client is putting together at one business: the services they approved with Continue
([Set booking services](set-booking-services.md)) and how many of each, in that order. Picks not yet approved stay on
the screen and never reach it. `InMemoryBookingPreparationDataSource` is a `single` holding one `BookingSelection`
per business id in a `MutableStateFlow`, so the approved services outlive the screens: opening the business again
restores them, and later steps read them from here. Nothing is persisted; they last until the page reloads or the
user logs out (the datasource is a `LogOutAction`). Each pick is matched against the business's
current services, so the emitted service carries the current name, duration and price, and a pick the business no
longer offers (deleted or unavailable) is left out.

```mermaid
flowchart TD
    F([flow businessId]) --> Sel[BookingPreparationDataSource.observeSelection businessId<br/>in-memory MutableStateFlow, empty when nothing approved]
    F --> Svc[GetBusinessServices.flow businessId]
    Sel --> C[combine]
    Svc --> C
    C --> Match[each picked serviceId → offered service where isAvailable<br/>not offered → left out]
    Match --> Out([BookingPreparation services with quantity])
```
