package me.bookk.feature.authorization.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.suspendCancellableCoroutine
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIApplication
import platform.darwin.NSObject

class IosPassKeyManager : PassKeyManager {

    @OptIn(BetaInteropApi::class)
    override suspend fun create(challenge: PassKeyManager.ChallengeRequest): PasskeyVerificationPayload =
        suspendCancellableCoroutine {
            val delegate = PasskeyControllerDelegate(it)
            val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(
                relyingPartyIdentifier = "bookkme.app"
            )
            val nsChallenge = NSString
                .create(string = challenge.challengeJson)
                .dataUsingEncoding(encoding = NSUTF8StringEncoding)!!
            val nsUserId = NSString
                .create(string = challenge.userId)
                .dataUsingEncoding(encoding = NSUTF8StringEncoding)!!
            val platformKeyRequest =
                platformProvider.createCredentialRegistrationRequestWithChallenge(
                    challenge = nsChallenge,
                    name = challenge.userName,
                    userID = nsUserId
                )
            val authController = ASAuthorizationController(listOf(platformKeyRequest))
            authController.delegate = delegate
            authController.presentationContextProvider =
                object : ASAuthorizationControllerPresentationContextProvidingProtocol, NSObject() {
                    override fun presentationAnchorForAuthorizationController(
                        controller: ASAuthorizationController
                    ): ASPresentationAnchor =
                        requireNotNull(UIApplication.sharedApplication.keyWindow)
                }
            authController.performRequests()
        }

    override suspend fun authorize(jsonChallenge: String): PasskeyVerificationPayload = suspendCancellableCoroutine {
        val delegate = PasskeyControllerDelegate(it)
        val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(
            relyingPartyIdentifier = "bookkme.app"
        )
        val nsChallenge = NSString
            .create(string = jsonChallenge)
            .dataUsingEncoding(encoding = NSUTF8StringEncoding)!!

        val platformKeyRequest = platformProvider.createCredentialAssertionRequestWithChallenge(
            challenge = nsChallenge
        )
        val authController = ASAuthorizationController(listOf(platformKeyRequest))
        authController.delegate = delegate
        authController.presentationContextProvider =
            object : ASAuthorizationControllerPresentationContextProvidingProtocol, NSObject() {
                override fun presentationAnchorForAuthorizationController(
                    controller: ASAuthorizationController
                ): ASPresentationAnchor =
                    requireNotNull(UIApplication.sharedApplication.keyWindow)
            }
        authController.performRequests()
    }
}