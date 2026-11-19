[← Operations](../README.md)

# Redeem employee invitation

`RedeemEmployeeInvitation(code)` → `POST /api/business/employee_invitation/redeem`
· called from [Join business](../business/join-business.md)

After joining, the use case re-syncs the business list selecting the joined business in the same transaction
(`RefreshBusinessInfo.refreshAndSelect`; the new business and the user's grants in it only exist on the server
so far), then calls Switch dashboard business, which only tells the server since the local selection is already
set. The business-error mapping
wraps the *whole* block. If the refresh or the switch fails after a successful redeem, the raw error
propagates, even though the user has already joined.

A business with its limit of active employees (100 unless the backend raises it; suspended employees don't count)
refuses the join with `BUSINESS_EMPLOYEES_LIMIT_REACHED` (200044).

```mermaid
flowchart TD
    Start([invoke code]) --> Net[EmployeeInvitationDataSource.redeemInvitation<br/>POST /api/business/employee_invitation/redeem]
    Net -- 2xx employee --> Refresh[RefreshBusinessInfo.refreshAndSelect employee.businessId<br/>list + dashboard_business in one transaction]
    Refresh --> Switch[SwitchDashboardBusiness employee.businessId]
    Switch --> R([return Employee])
    Net -- business error --> Code{errorCode}
    Code -- 200017 INVITATION_ALREADY_PROCESSED --> E1([throw Error.AlreadyProcessed])
    Code -- 200018 EMPLOYEE_EXISTS --> E2([throw Error.EmployeeExists])
    Code -- 200044 EMPLOYEES_LIMIT_REACHED --> E3([throw Error.EmployeesLimitReached])
    Code -- other --> EX([rethrow])
    Refresh -- error --> EX
```

Composes [Refresh business info](../business/refresh-business-info.md) and [Switch dashboard business](../business/switch-dashboard-business.md).
