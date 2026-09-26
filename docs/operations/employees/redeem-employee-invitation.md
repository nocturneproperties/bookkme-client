[← Operations](../README.md)

# Redeem employee invitation

`RedeemEmployeeInvitation(code)` → `POST /api/business/employee_invitation/redeem`
· called from [Join business](../business/join-business.md)

After joining, the use case re-syncs the business list (the new business and the user's grants in it only
exist on the server so far) and then switches the dashboard to that business. The business-error mapping
wraps the *whole* block. If the refresh or the switch fails after a successful redeem, the raw error
propagates, even though the user has already joined.

```mermaid
flowchart TD
    Start([invoke code]) --> Net[EmployeeInvitationDataSource.redeemInvitation<br/>POST /api/business/employee_invitation/redeem]
    Net -- 2xx employee --> Refresh[RefreshBusinessInfo]
    Refresh --> Switch[SwitchDashboardBusiness employee.businessId]
    Switch --> R([return Employee])
    Net -- business error --> Code{errorCode}
    Code -- 200017 INVITATION_ALREADY_PROCESSED --> E1([throw Error.AlreadyProcessed])
    Code -- 200018 EMPLOYEE_EXISTS --> E2([throw Error.EmployeeExists])
    Code -- other --> EX([rethrow])
    Refresh -- error --> EX
```

Composes [Refresh business info](../business/refresh-business-info.md) and [Switch dashboard business](../business/switch-dashboard-business.md).
