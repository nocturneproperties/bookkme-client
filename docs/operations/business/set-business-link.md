[← Operations](../README.md)

# Set business link

`SetBusinessLink(businessId, name)` → `BusinessLink`, **network → Room**
· called from `BusinessLinkViewModel` (Save)

Trims and lowercases the name (`BusinessLink.normalizeName`, which the screen's preview also uses) and checks it against the backend's rule (3 to 32 latin letters or digits,
single hyphens between them, `BusinessLink.isValidName`) before any request, so an invalid name fails without a round trip.
The screen uses the same `isValidName` to keep Save disabled until the typed name passes it. Only the
owner may set the link: the backend answers a plain `404` for anyone else, which becomes `NotOwner`.
The backend keeps a replaced name for 90 days (it still opens the business and no one else can take it)
and allows at most 3 changes within 90 days. The saved link is upserted into `business_link`, which the
screen observes through [Get business link](get-business-link.md); a failed save leaves the cached row as it was.

```mermaid
flowchart TD
    Start([invoke businessId, name]) --> Norm[name = trim and lowercase]
    Norm --> Valid{matches 3 to 32 of a-z 0-9 with single hyphens between?}
    Valid -- no --> EI([throw Error.Invalid])
    Valid -- yes --> Net[BusinessLinkDataSource.setLink<br/>PUT /api/business/businessId/link<br/>BusinessLinkRequest name]
    Net -- 200 BusinessLinkRemote --> Save[BusinessLinkDataSource.saveLinkInDb<br/>BusinessLinkDao.upsert]
    Save --> R([return BusinessLink businessId, name])
    Net -- 404 without error code --> ENO([throw Error.NotOwner])
    Net -- business error --> Code{errorCode}
    Code -- 200049 BUSINESS_LINK_INVALID --> EI
    Code -- 200050 BUSINESS_LINK_RESERVED --> ER([throw Error.Reserved])
    Code -- 200051 BUSINESS_LINK_TAKEN --> ET([throw Error.Taken])
    Code -- 200052 BUSINESS_LINK_CHANGE_LIMIT_REACHED --> EC([throw Error.ChangeLimitReached])
    Code -- other --> EX([rethrow])
    Net -- network / other --> EX
```

Read through [Get business link](get-business-link.md).
