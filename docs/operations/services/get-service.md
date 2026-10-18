[← Operations](../README.md)

# Get service

`GetService(id)` → **local only**, `serviceDao.getById(id)` · called from `EditServiceViewModel`

Reads the cached `service` row with its group. There is no network call: the services list is synced by
[Get services](get-services.md) before a service can be opened.

```mermaid
flowchart TD
    Start([invoke id]) --> DB[(ServiceDataSource.getServiceFromDb<br/>serviceDao.getById id)]
    DB -- null --> NF([throw GetService.Error.NotFound])
    DB -- row --> R([return Service])
```
