[← Operations](../README.md)

# Is business owner

`IsBusinessOwner(employee)` → local only · called from `EditEmployeeViewModel` and [Update employee](update-employee.md)

A wrapper in the employees domain so `feature/employees/presentation` does not depend on the business domain.
It takes the first emission of `ObserveUserBusinessesChanges` (the cached `business` rows), finds the
employee's business and compares its `ownerId` with the employee's `userId`. A business that is not cached, or a
row cached before `ownerId` existed (`NULL` until the next refresh), counts as "not the owner".

```mermaid
flowchart TD
    Start([invoke employee]) --> Db[(ObserveUserBusinessesChanges.first<br/>businessDao.observeAllBusinesses)]
    Db --> Find{business with id == employee.businessId}
    Find -- none --> F([return false])
    Find -- found --> Cmp{business.ownerId == employee.userId}
    Cmp -- yes --> T([return true])
    Cmp -- no or ownerId null --> F
```
