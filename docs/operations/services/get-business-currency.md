[← Operations](../README.md)

# Get business currency

`GetBusinessCurrency(businessId)` → **local only**, `businessDao.queryBusiness(businessId).currencyCode`
· called from `AddServiceViewModel`

`ServiceDataSourceImpl` reads the `business` table owned by the business feature directly, going through the
shared `BusinessDao`. The business must be cached. Otherwise `requireNotNull` throws.

```mermaid
flowchart TD
    Start([invoke businessId]) --> DB[(businessDao.queryBusiness businessId)]
    DB -- null --> EX([throw IllegalArgumentException])
    DB -- row --> Map[Money.SupportedCurrency.fromCode currencyCode]
    Map --> R([return currency])
```
