[← Operations](../README.md)

# Resolve business link

`ResolveBusinessLink(name)` → `BusinessLink`, **network only**
· called from `ClientHomeViewModel` (client web app, Telegram `startapp` launch parameter)

Finds the business behind a link name, so a `t.me/<bot>/<app>?startapp=<name>` launch opens and saves that business
(the ViewModel then calls [Save business](../appointments/save-business.md)). Trims and lowercases the name and
checks it against the link name rule (`BusinessLink.normalizeName`, `BusinessLink.isValidName`) before any request,
so a launch parameter that cannot be a link name fails without a round trip. The lookup needs no sign-in and is
rate limited by nginx (30 a minute per address). A name the business replaced within the last 90 days still finds it,
and the response carries the current name. Nothing is cached: it is a one-off lookup on launch, not screen data.

```mermaid
flowchart TD
    Start([invoke name]) --> Norm[name = trim and lowercase]
    Norm --> Valid{matches 3 to 32 of a-z 0-9 with single hyphens between?}
    Valid -- no --> ENF([throw Error.NotFound])
    Valid -- yes --> Net[BusinessDataSource.resolveLink<br/>GET /api/business/link/name]
    Net -- 200 BusinessLinkRemote --> R([return BusinessLink businessId, current name])
    Net -- business error --> Code{errorCode}
    Code -- 200053 BUSINESS_LINK_NOT_FOUND --> ENF
    Code -- other --> EX([rethrow])
    Net -- 429 / network / other --> EX
```
