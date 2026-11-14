[← Operations](../README.md)

# Confirm email change

`ConfirmEmailChange(change, code)` → `POST /api/auth/email/confirm`
· called from `VerifyEmailViewModel` in email-change mode

Sends the verification id of the newest [Start email change](start-email-change.md) with the typed code.
On 204 the backend has stored the new address; the use case then writes the confirmed address into the
cached `user_profile` row, so Settings, Edit Profile and the dashboard observe it without another
request. It does not re-fetch `GET /api/user/me`: a network failure after a successful confirmation
would otherwise report a change that already happened as failed. Without a cached profile nothing is
written; the next `UserProfileCRUD.get()` loads it from the backend.

`EMAIL_EXIST` means another account took the address after the code was sent. The code stays usable on
the backend, so the verify screen keeps it and lets the user go back to pick another address.

```mermaid
flowchart TD
    Start([invoke change, code]) --> Post[EmailChangeDataSource.confirmEmailChange<br/>POST /api/auth/email/confirm<br/>EmailConfirmation verificationId = 1, code = 2]
    Post -- 204 --> Get[UserProfileDataSource.getProfileFromDatabase]
    Get -- profile --> Up[UserProfileDataSource.upsertProfile<br/>profile.copy email = change.email]
    Get -- none --> R([Unit])
    Up --> R
    Post -- 422 EMAIL_CODE_INVALID 910001 --> E1([throw EmailVerificationError.CodeInvalid])
    Post -- 422 EMAIL_CODE_EXPIRED 910002 --> E2([throw EmailVerificationError.CodeExpired])
    Post -- 422 EMAIL_CODE_ATTEMPTS_EXCEEDED 910003 --> E3([throw EmailVerificationError.AttemptsExceeded])
    Post -- 422 EMAIL_EXIST 1 --> E4([throw Error.EmailAlreadyExist])
    Post -- other business error, nginx 429 TooManyRequests, connection --> EX([rethrow, profile untouched])
```
