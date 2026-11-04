[← Operations](../README.md)

# Delete client with passkey confirmation

`DeleteClient(client)` confirms the action, deletes remotely, then invalidates local records.

```mermaid
flowchart TD
    Start([invoke client]) --> Confirm[ConfirmSensitiveAction<br/>DELETE_CLIENT, client.businessId, client.id]
    Confirm -- shared Error.Ignore --> Cancel([propagate cancellation])
    Confirm -- confirmation error --> Failed([throw Error.ConfirmationFailed])
    Confirm -- token --> Net[ClientsDataSource.deleteClient businessId, id, token<br/>DELETE /api/business/businessId/clients/id<br/>body DeleteClientRequest stepUpToken]
    Net -- 200040 BUSINESS_STEP_UP_INVALID --> Required([throw Error.ConfirmationRequired])
    Net -- other failure --> Error([propagate; keep local cache])
    Net -- 204 --> Del[(ClientsDataSource.deleteClientInDb<br/>ClientsDao.deleteWithAppointmentCaches transaction)]
    Del --> App[(DELETE appointment WHERE clientId<br/>service and adjustment children CASCADE)]
    App --> Req[(DELETE appointment_request WHERE clientId<br/>request services CASCADE)]
    Req --> Client[(DELETE client WHERE id)] --> R([Unit])
```

Composes [Confirm sensitive action](../authorization/confirm-sensitive-action.md). A cancelled or failed
confirmation makes no DELETE request. A failed remote DELETE never removes local records. After success,
whole cached appointment/request aggregates referencing the deleted client are evicted, so personal
snapshots cannot linger in history or request lists. A later refresh can restore the backend's erased
versions. No anonymization values are invented locally. No schema version change is needed for these
DAO write-path changes, and logout behavior is unchanged.
