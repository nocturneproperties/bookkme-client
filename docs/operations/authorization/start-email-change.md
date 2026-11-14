[← Operations](../README.md)

# Start email change

`StartEmailChange(email)` → `POST /api/auth/email`
· called from `ChangeEmailViewModel` ("Send code") and from `VerifyEmailViewModel` in email-change mode
("Send a new code")

Trims the address and asks the backend to email a 6-digit code (valid for 10 minutes) to it. The address
is not stored until the code is confirmed with [Confirm email change](confirm-email-change.md). Calling it
again for the same address is the resend: the backend starts a new verification and only the newest code
works. Nothing is cached locally; the returned `PendingEmailChange` (address + verification id and times)
is passed to the verify screen as its navigation argument.

```mermaid
flowchart TD
    Start([invoke email]) --> Trim[email.trim]
    Trim --> Post[EmailChangeDataSource.startEmailChange<br/>POST /api/auth/email<br/>EmailChangeRequest email = 1]
    Post -- EmailVerificationStarted --> R([PendingEmailChange<br/>trimmed email, verification id, expiresAt, resendAvailableAt])
    Post -- 422 EMAIL_EXIST 1 --> E1([throw Error.EmailAlreadyExist])
    Post -- 422 INVALID_EMAIL_FORMAT 2 --> E2([throw Error.InvalidEmailFormat])
    Post -- 422 EMAIL_UNDELIVERABLE 910005 --> E3([throw EmailVerificationError.Undeliverable])
    Post -- 429 EMAIL_CODE_RESEND_TOO_SOON 910004 --> E4([throw EmailVerificationError.ResendTooSoon])
    Post -- other business error, nginx 429 TooManyRequests, connection --> EX([rethrow])
```
