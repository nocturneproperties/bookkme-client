package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.SensitiveActionConfirmation
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class ConfirmSensitiveActionImpl(
    private val dataSource: AuthorizationDataSource,
    private val passKeyManager: PassKeyManager,
) : ConfirmSensitiveAction {
    override suspend fun invoke(action: SensitiveAction, businessId: Uuid, targetId: Uuid): String {
        val challenge = dataSource.getAuthorizationChallenge()
        val payload = runCatching {
            passKeyManager.authorize(
                PassKeyManager.AuthorizationRequest(
                    challengeJson = challenge.challengeJson,
                    challenge = challenge.challenge,
                ),
            )
        }
            .getOrElse {
                throw when (it) {
                    is PassKeyManager.Error.UserCancelled -> DomainError.Ignore(it)
                    is PassKeyManager.Error -> ConfirmSensitiveAction.Error.VerificationFailed()
                    else -> it
                }
            }
        return runCatching {
            dataSource.confirmSensitiveAction(
                SensitiveActionConfirmation(
                    requestId = challenge.requestId,
                    publicKeyCredentialJson = payload.jsonPayload,
                    action = action,
                    businessId = businessId,
                    targetId = targetId,
                ),
            )
        }
            .onBusinessError {
                throw when (it.errorCode) {
                    AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED ->
                        ConfirmSensitiveAction.Error.ChallengeExpired()

                    AuthErrorCodes.VERIFICATION_FAILED ->
                        ConfirmSensitiveAction.Error.VerificationFailed()

                    AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND ->
                        ConfirmSensitiveAction.Error.PasskeyNotFound()

                    AuthErrorCodes.PASSKEY_NOT_OWNED ->
                        ConfirmSensitiveAction.Error.PasskeyNotOwned()

                    else -> it
                }
            }
            .getOrThrow()
    }
}
