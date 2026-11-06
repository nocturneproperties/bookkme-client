[← Operations](../README.md)

# Delete client with passkey confirmation

`DeleteClient(client)` confirms the action, deletes remotely, then invalidates local records.

Before invoking it, the Android and iOS edit screen shows the shared confirmation dialog explaining that
client deletion also erases personal data from the business's appointments, cannot be undone and needs
passkey confirmation. Cancel leaves the client unchanged. The delete action is visible to the business
owner for integrated clients, and follows the ordinary delete permission for detached clients; see
[Get clients permissions](get-clients-permissions.md). Successful deletion plays a success haptic and
returns to the client list; failure preserves the screen without a success haptic.

```mermaid
flowchart TD
    Start([invoke client]) --> Confirm[ConfirmSensitiveAction<br/>DELETE_CLIENT, client.businessId, client.id]
    Confirm -- shared Error.Ignore --> Cancel([propagate cancellation])
    Confirm -- confirmation error --> Failed([throw Error.ConfirmationFailed])
    Confirm -- token --> Net[ClientsDataSource.deleteClient businessId, id, token<br/>DELETE /api/business/businessId/clients/id<br/>body DeleteClientRequest confirmationToken]
    Net -- 200040 BUSINESS_ACTION_CONFIRMATION_INVALID --> Required([throw Error.ConfirmationRequired])
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
