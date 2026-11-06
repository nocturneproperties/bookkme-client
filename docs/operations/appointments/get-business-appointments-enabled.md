[← Operations](../README.md)

# Get business appointments enabled

`GetBusinessAppointmentsEnabled.invoke()` reads the current backend records without persisting them.
There is no local cache or background observer. Empty results are returned unchanged.
Errors propagate through the standard datasource exception mapping; this operation adds no domain errors.

```mermaid
flowchart TD
    Start([invoke]) --> Fetch["BusinessAppointmentsDataSource.getEnabled<br/>GET /api/appointments/enabled"]
    Fetch --> Map[Map remote records to domain records]
    Map --> Result([Return list])
    Fetch -- failure --> Error([Propagate mapped error])
```
