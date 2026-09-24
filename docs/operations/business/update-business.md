[← Operations](../README.md)

# Update business

`UpdateBusiness(model: Business.Update)` → `PUT /api/business/{id}`
· called from `BusinessSettingsViewModel`

Reads the cached business first and fails with `IllegalArgumentException` (`requireNotNull`) if it is not
cached. It copies the editable fields over the cached row, so fields that are not editable, such as the
permission columns and the time zone, are preserved. The PUT returns no body, so the **locally merged value**
is what gets written back to Room, not a server response.

```mermaid
flowchart TD
    Start([invoke model]) --> Read[(BusinessDataSource.getBusinessById model.id)]
    Read -- null --> E0([throw IllegalArgumentException])
    Read -- current --> Merge[current.copy name, description, address, location,<br/>currency, socials, schedule]
    Merge --> Net[BusinessDataSource.updateBusiness<br/>PUT /api/business/id]
    Net -- 2xx --> Save[(saveBusinessInDB merged<br/>upsertWithChildren)]
    Save --> R([return merged Business])
    Net -- business error --> Code{errorCode}
    Code -- 200019 BUSINESS_ACTIVE_DAY_WITHOUT_WORK_HOURS --> E1([throw Error.ActiveDayWithoutWorkHours])
    Code -- 200020 BUSINESS_INVALID_DAY_OFF_RANGE --> E2([throw Error.InvalidDayOffRange])
    Code -- other --> EX([rethrow])
```
