[← Operations](../README.md)

# Refresh token

`RefreshToken(refreshToken)` → `POST /api/auth/refresh` (unauthenticated client, `Authorization: Bearer <refresh>`)
· called only from the Ktor `bearer { refreshTokens { … } }` block in `shared/.../NetworkDI.kt`

The caller runs it under a `Mutex`. After taking the lock it re-reads the tokens, and if another request has
already refreshed them while this one waited, it reuses those tokens instead of refreshing again. If the refresh
fails, the block returns `null`. Ktor then passes the 401 on, the error maps to `Error.Unauthorized`, screens post
`PresentationNotification.Unauthorized`, and that leads to [Log out](log-out.md).

```mermaid
flowchart TD
    K([Ktor gets 401]) --> Cur[GetTokenInfo]
    Cur -- null --> N([return null])
    Cur -- current --> Lock[[refreshMutex.withLock]]
    Lock --> Again[GetTokenInfo]
    Again -- changed and not null --> Reuse([return tokensAfterLock])
    Again -- unchanged --> UC[RefreshToken current.refreshToken]
    UC --> Net[AuthorizationDataSource.refreshToken<br/>POST /api/auth/refresh]
    Net -- 2xx --> Save[authorization_prefs: access_token, refresh_token]
    Save --> Ok([return BearerTokens])
    Net -- error --> Fail([return null → request fails with 401])
```
