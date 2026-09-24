[← Operations](../README.md)

# Join business

`JoinBusiness(code)` → wraps `employees`' [Redeem employee invitation](../employees/redeem-employee-invitation.md)
· called from `BusinessDashboardViewModel`

This wrapper exists so that `feature/business/presentation` never depends on `feature.employees.domain.api`
(see **Minimize cross-feature domain dependencies** in `AGENTS.md`). It only translates error types.

```mermaid
flowchart TD
    Start([invoke code]) --> Redeem[RedeemEmployeeInvitation code]
    Redeem -- ok --> R([Unit])
    Redeem -- Error.AlreadyProcessed --> E1([throw JoinBusiness.Error.AlreadyProcessed])
    Redeem -- Error.EmployeeExists --> E2([throw JoinBusiness.Error.EmployeeExists])
    Redeem -- other --> EX([rethrow])
```
