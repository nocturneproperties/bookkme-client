[← Operations](../README.md)

# Switch dashboard business

`SwitchDashboardBusiness(businessId: Uuid?)` → `PUT /api/business/{id}/dashboard`
· called from `BusinessDashboardViewModel`, `DashboardViewModel` (business picked on the Get started screen),
[Create business](create-business.md), [Redeem employee invitation](../employees/redeem-employee-invitation.md)
and [Initiate business suspend](../authorization/initiate-business-suspend.md) (with `null`)

A `null` id clears the local selection and skips the server call. The `dashboard_business` row has a foreign
key to `business`, so the id must already be cached; every caller picks it from the cached list or right after
[Refresh business info](refresh-business-info.md) selected it.

Local-first and best-effort: the `dashboard_business` row is written right away, which switches every
observing screen,
and a failure of the server call is **silently ignored**. The server copy is only read back by the
`rawFetch()` after the next sign-in.

```mermaid
flowchart TD
    Start([invoke businessId]) --> Local[(dashboard_business := businessId<br/>or cleared when null)]
    Local --> Null{businessId null?}
    Null -- yes --> R
    Null -- no --> Net[runCatching BusinessDataSource.setDashboardBusinessOnRemote<br/>PUT /api/business/id/dashboard]
    Net -- ok or error --> R([Unit])
```
