package me.bookk.feature.authorization.data.local

import kotlinx.coroutines.CancellableContinuation
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import okio.ByteString.Companion.toByteString
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationErrorFailed
import platform.AuthenticationServices.ASAuthorizationErrorNotHandled
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialAssertion
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialRegistration
import platform.AuthenticationServices.ASAuthorizationPublicKeyCredentialAttachment
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

private val PlatformAttachment = ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentPlatform
private val CrossPlatformAttachment = ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentCrossPlatform

class PasskeyControllerDelegate(
    private val continuation: CancellableContinuation<PasskeyVerificationPayload>
): NSObject(), ASAuthorizationControllerDelegateProtocol {

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization
        ) {
            when (val cred = didCompleteWithAuthorization.credential) {
                is ASAuthorizationPlatformPublicKeyCredentialRegistration -> {
                    continuation.resume(PasskeyVerificationPayload(
                        """
                            {
                              "id": "${cred.credentialID.toByteString().base64Url()}",
                              "rawId": "${cred.credentialID.toByteString().base64Url()}",
                              "authenticatorAttachment": "${if (cred.attachment == PlatformAttachment) "platform" else "cross-platform"}",
                              "type": "public-key",
                              "response": {
                                "clientDataJSON": "${cred.rawClientDataJSON.toByteString().base64Url()}",
                                "attestationObject": "${cred.rawAttestationObject?.toByteString()?.base64Url().orEmpty()}",
                                "transports": [
                                  "internal",
                                  "hybrid"
                                ]
                              },
                              "clientExtensionResults": {}
                            }
                        """.trimIndent()
                    ))
                }

                is ASAuthorizationPlatformPublicKeyCredentialAssertion -> {
                    continuation.resume(PasskeyVerificationPayload(
                        """
                            {
                              "id": "${cred.credentialID.toByteString().base64Url()}",
                              "rawId": "${cred.credentialID.toByteString().base64Url()}",
                              "authenticatorAttachment": "${if (cred.attachment == PlatformAttachment) "platform" else "cross-platform"}",
                              "type": "public-key",
                              "response": {
                                "clientDataJSON": "${cred.rawClientDataJSON.toByteString().base64Url()}",
                                "authenticatorData": "${cred.rawAuthenticatorData?.toByteString()?.base64Url().orEmpty()}",
                                "signature": "${cred.signature?.toByteString()?.base64Url().orEmpty()}",
                                "userHandle": "${cred.userID?.toByteString()?.base64Url().orEmpty()}"
                              },
                              "clientExtensionResults": {}
                            }
                        """.trimIndent()
                    ))
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
                ASAuthorizationErrorCanceled -> PassKeyManager.Error.UserCancelled()

                ASAuthorizationErrorFailed,
                ASAuthorizationErrorNotHandled -> PassKeyManager.Error.Infrastructure()

                else -> PassKeyManager.Error.Unknown(Throwable(didCompleteWithError.code.toString()))
            }
            continuation.cancel(error)
        }
    }