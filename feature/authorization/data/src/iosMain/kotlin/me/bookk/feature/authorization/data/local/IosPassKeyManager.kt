package me.bookk.feature.authorization.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.suspendCancellableCoroutine
import me.bookk.core.toNSData
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASPresentationAnchor
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.io.encoding.Base64

class IosPassKeyManager(val relyingParty: String) : PassKeyManager {

    private var delegate: PasskeyControllerDelegate? = null

    @OptIn(BetaInteropApi::class)
    override suspend fun create(challenge: PassKeyManager.CreationRequest): PasskeyVerificationPayload =
        suspendCancellableCoroutine {
            delegate = PasskeyControllerDelegate(it)
            val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(
                relyingPartyIdentifier = relyingParty
            )
            val nsChallenge = Base64.UrlSafe
                .withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                .decode(challenge.challenge)
                .toNSData()
            val nsUserId = Base64.UrlSafe
                .withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                .decode(challenge.userId)
                .toNSData()
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
        }.also {
            delegate = null
        }

    override suspend fun authorize(challenge: PassKeyManager.AuthorizationRequest): PasskeyVerificationPayload =
        suspendCancellableCoroutine {
            delegate = PasskeyControllerDelegate(it)
            val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(
                relyingPartyIdentifier = relyingParty
            )
            val nsChallenge = Base64.UrlSafe
                .withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                .decode(challenge.challenge)
                .toNSData()

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
        }.also {
            delegate = null
        }
}
