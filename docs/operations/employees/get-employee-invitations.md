[← Operations](../README.md)

# Get employee invitations

`GetEmployeeInvitations.flow(businessId)` / `.refresh(businessId)` →
`GET /api/business/{businessId}/employee_invitation` · called from `InviteEmployeeViewModel`

The same list steps as [Get clients list](../clients/get-clients-list.md). Unlike most lists, `flow` takes an
explicit `businessId` instead of following the dashboard business.

```mermaid
flowchart TD
    F([flow businessId]) --> Obs[(employeeInvitationDao.observeInvitations businessId)] --> FR([emit List])

    R([refresh businessId]) --> Net[EmployeeInvitationDataSource.getInvitations<br/>GET /api/business/businessId/employee_invitation]
    Net --> Ids[(getInvitationIdsInDb)]
    Ids --> Stale{ids not in response?}
    Stale -- yes --> Del[(deleteInvitationsInDb staleIds)]
    Stale -- no --> Save
    Del --> Save[(saveInvitationsInDb)]
    Save --> Mark[employee_invitations_prefs: last_synced_at_businessId]
    Mark --> RR([return List])
```
