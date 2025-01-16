package me.bookk.feature.authorization.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationError
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationErrorFailed
import platform.AuthenticationServices.ASAuthorizationErrorNotHandled
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialAssertion
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialRegistration
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIApplication
import platform.darwin.NSObject

actual class PassKeyCreator : NSObject(), ASAuthorizationControllerDelegateProtocol {

    private val uiContext =
        object : ASAuthorizationControllerPresentationContextProvidingProtocol, NSObject() {
            override fun presentationAnchorForAuthorizationController(
                controller: ASAuthorizationController
            ): ASPresentationAnchor = requireNotNull(UIApplication.sharedApplication.keyWindow)
        }
    private var continuation: CancellableContinuation<PasskeyVerificationPayload>? = null

    @OptIn(BetaInteropApi::class)
    actual suspend fun create(challenge: ServerChallenge): PasskeyVerificationPayload =
        suspendCancellableCoroutine {
            continuation = it
            val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(
                relyingPartyIdentifier = "bookkk.me"
            )
            val nsChallenge = NSString
                .create(string = challenge.jsonChallengeData)
                .dataUsingEncoding(encoding = NSUTF8StringEncoding)!!
            val nsUserId = NSString
                .create(string = challenge.userId)
                .dataUsingEncoding(encoding = NSUTF8StringEncoding)!!
            val platformKeyRequest =
                platformProvider.createCredentialRegistrationRequestWithChallenge(
                    challenge = nsChallenge,
                    name = challenge.displayName,
                    userID = nsUserId
                )
            val authController = ASAuthorizationController(listOf(platformKeyRequest))
            authController.delegate = this
            authController.presentationContextProvider = uiContext
            authController.performRequests()
        }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        when (val cred = didCompleteWithAuthorization.credential) {
            is ASAuthorizationPlatformPublicKeyCredentialRegistration -> {
                continuation?.cancel(UnsupportedOperationException())
            }

            is ASAuthorizationPlatformPublicKeyCredentialAssertion -> {
                continuation?.cancel(UnsupportedOperationException())
            }

            else -> {
                continuation?.cancel(UnsupportedOperationException())
            }
        }
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        val error = when (didCompleteWithError.code) {
            ASAuthorizationErrorCanceled -> PasskeyVerificationError.UserCancelled

            ASAuthorizationErrorFailed,
            ASAuthorizationErrorNotHandled -> PasskeyVerificationError.Infrastructure

            else -> PasskeyVerificationError.Unknown
        }
        continuation?.cancel(error)
    }
}