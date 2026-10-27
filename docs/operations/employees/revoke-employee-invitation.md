[← Operations](../README.md)

# Revoke employee invitation

`RevokeEmployeeInvitation(businessId, id)` → `POST /api/business/{businessId}/employee_invitation/{id}/revoke`
· called from `InviteEmployeeViewModel`

Network only. The cached invitation row keeps its old status until the next [Get employee
invitations](get-employee-invitations.md) refresh.

```mermaid
flowchart TD
    Start([invoke businessId, id]) --> Net[EmployeeInvitationDataSource.revokeInvitation<br/>POST .../employee_invitation/id/revoke]
    Net -- 2xx --> R([Unit])
    Net -- business error --> Code{errorCode}
    Code -- 200017 INVITATION_ALREADY_PROCESSED --> E1([throw Error.AlreadyProcessed])
    Code -- other --> EX([rethrow])
```
