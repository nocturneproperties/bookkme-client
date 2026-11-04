package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

interface ConfirmSensitiveAction {
    suspend operator fun invoke(action: SensitiveAction, businessId: Uuid, targetId: Uuid): String

    sealed interface Error {
        class ChallengeExpired : Exception(), Error
        class VerificationFailed : Exception(), Error
        class PasskeyNotFound : Exception(), Error
        class PasskeyNotOwned : Exception(), Error
    }
}
