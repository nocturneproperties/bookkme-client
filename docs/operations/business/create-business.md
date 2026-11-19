[← Operations](../README.md)

# Create business

`CreateBusiness(name)` → `POST /api/business`
· called from `BusinessDashboardViewModel` (business switcher) and `DashboardViewModel` (Home onboarding), each from a name input dialog

The name is trimmed first. A blank name throws `CreateBusiness.Error.EmptyName` before any network call.
The currency comes from the device region (`DeviceFacade.getCurrencyCode()`). If the device has no
currency, or its currency is not in `Money.SupportedCurrency`, the use case falls back to `USD`. The time zone is
taken from the device. The owner can change the currency later in Business settings. The created business is not written to the database directly. Instead the use case re-syncs the whole
business list selecting the new business in the same transaction (`RefreshBusinessInfo.refreshAndSelect`), so
the new row and its permission columns come from `GET /api/business/mine` and appear together with the
selection. [Switch dashboard business](switch-dashboard-business.md) then only tells the server; its local
write is a no-op because the business is already selected.

A user can own several businesses. Once they own their limit (20 unless the backend raises it for them), the
backend answers `BUSINESS_OWNED_BUSINESSES_LIMIT_REACHED` (200045). Both callers show it as a message. The backend
no longer sends `BUSINESS_ALREADY_EXIST` (200001), so the use case does not map it.

```mermaid
flowchart TD
    Start([invoke name]) --> Trim[name = name.trim]
    Trim --> Blank{name is empty}
    Blank -- yes --> E0([throw CreateBusiness.Error.EmptyName])
    Blank -- no --> Cur[DeviceFacade.getCurrencyCode]
    Cur --> Supported{code in Money.SupportedCurrency}
    Supported -- yes --> Device[currency = device currency]
    Supported -- no or null --> Fallback[currency = USD]
    Device --> Net[BusinessDataSource.createBusiness name, currency, device TimeZone<br/>POST /api/business]
    Fallback --> Net
    Net -- 2xx business --> Refresh[RefreshBusinessInfo.refreshAndSelect business.id<br/>list + dashboard_business in one transaction]
    Refresh --> Switch[SwitchDashboardBusiness business.id]
    Switch --> R([return Business])
    Net -- business error --> Code{errorCode}
    Code -- 200002 --> Invalid([throw Error.InvalidName])
    Code -- 200035 --> Currency([throw Error.InvalidCurrency])
    Code -- 200045 OWNED_BUSINESSES_LIMIT_REACHED --> Limit([throw Error.OwnedBusinessesLimitReached])
    Code -- other --> EX([rethrow])
    Refresh -- error --> EX
```

Composes [Refresh business info](refresh-business-info.md) and [Switch dashboard business](switch-dashboard-business.md).
