[← Operations](../README.md)

# Sign in with Telegram

`SignInWithTelegram(initData, acceptedDocuments)` → `POST /api/auth/telegram/sign_in`
· no caller yet (the website's `/telegram` Mini App needs the authorization data layer on JS first)

`initData` is the raw `Telegram.WebApp.initData` string, passed through unparsed: the server checks
Telegram's Ed25519 signature for its bot and accepts the data for one hour after `auth_date`. A
returning Telegram user signs in and the server ignores `acceptedDocuments`. A Telegram user seen for
the first time gets a new account with the placeholder email
`<telegram user id>@telegramundeliverable.org`. The first call sends `acceptedDocuments = null`, and
the caller sends the document versions in effect only after `LegalDocumentsNotAccepted`. After the
token step it follows [Sign in](sign-in.md): the same "`authorized` is written last" rule and the
same partial-failure behaviour.

```mermaid
flowchart TD
    Start([invoke initData, acceptedDocuments]) --> Dev[DeviceDataSource.getOrCreateDeviceUUID<br/>+ DeviceFacade.getDeviceName]
    Dev --> Req[AuthorizationDataSource.signInWithTelegram<br/>POST /api/auth/telegram/sign_in]
    Req -- business error --> C{errorCode}
    C -- 14 TELEGRAM_DATA_INVALID --> E1([throw Error.TelegramDataInvalid])
    C -- 15 TELEGRAM_DATA_EXPIRED --> E2([throw Error.TelegramDataExpired])
    C -- 16 TELEGRAM_SIGN_IN_UNAVAILABLE, HTTP 503 --> E3([throw Error.Unavailable])
    C -- 5 ACCOUNT_CREATION_FAILED --> E4([throw Error.AccountCreationFailed])
    C -- 100003 LEGAL_DOCUMENT_VERSION_UNKNOWN --> E5([throw Error.LegalDocumentVersionUnknown])
    C -- 100004 LEGAL_DOCUMENTS_NOT_ACCEPTED --> E6([throw Error.LegalDocumentsNotAccepted])
    C -- other --> EX([rethrow])
    Req -- other failure --> EX
    Req -- tokens --> Save[authorization_prefs: access_token, refresh_token]
    Save --> Inv[invalidateClientTokens]
    Inv --> Fetch[InitialAppDataFetch.rawFetch]
    Fetch --> Auth[authorization_prefs: authorized := true]
    Auth --> R([Unit])
```
