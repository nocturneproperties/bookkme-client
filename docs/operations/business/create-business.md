[← Operations](../README.md)

# Create business

`CreateBusiness(name)` → `POST /api/business`
· called from `CreateBusinessViewModel` and `DashboardViewModel`

The currency is **hard-coded to `"UAH"`** (a temporary placeholder in code) and the time zone is taken from the
device. The created business is not written to the database directly. Instead the use case re-syncs the whole
business list and then selects the new business, so the new row and its permission columns come from
`GET /api/business`.

```mermaid
flowchart TD
    Start([invoke name]) --> Net[BusinessDataSource.createBusiness name, UAH, device TimeZone<br/>POST /api/business]
    Net -- 2xx business --> Refresh[RefreshBusinessInfo<br/>applyDashboardIdFromRemote = false]
    Refresh --> Switch[SwitchDashboardBusiness business.id]
    Switch --> R([return Business])
    Net -- error --> EX([rethrow])
    Refresh -- error --> EX
```

Composes [Refresh business info](refresh-business-info.md) and [Switch dashboard business](switch-dashboard-business.md).
