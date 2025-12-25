package me.bookk.feature.authorization.data.local

import kotlinx.coroutines.CancellableContinuation
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationErrorFailed
import platform.AuthenticationServices.ASAuthorizationErrorNotHandled
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialAssertion
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialRegistration
import platform.Foundation.NSError
import platform.darwin.NSObject

class PasskeyControllerDelegate(
    private val continuation: CancellableContinuation<PasskeyVerificationPayload>
): NSObject(), ASAuthorizationControllerDelegateProtocol {

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization
        ) {
            when (val cred = didCompleteWithAuthorization.credential) {
                is ASAuthorizationPlatformPublicKeyCredentialRegistration -> {
                    continuation.cancel(UnsupportedOperationException())
                }

                is ASAuthorizationPlatformPublicKeyCredentialAssertion -> {
                    continuation.cancel(UnsupportedOperationException())
                }

                else -> {
                    continuation.cancel(UnsupportedOperationException())
                }
            }
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError
        ) {
            val error = when (didCompleteWithError.code) {
                ASAuthorizationErrorCanceled -> PassKeyManager.Error.UserCancelled

                ASAuthorizationErrorFailed,
                ASAuthorizationErrorNotHandled -> PassKeyManager.Error.Infrastructure

                else -> PassKeyManager.Error.Unknown(Throwable(didCompleteWithError.code.toString()))
            }
            continuation.cancel(error)
        }
    }