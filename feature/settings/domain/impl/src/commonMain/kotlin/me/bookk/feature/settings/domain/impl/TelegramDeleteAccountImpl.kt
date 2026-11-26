package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.domain.api.entity.ActionOutcome

internal class TelegramDeleteAccountImpl(
    startConfirmation: StartTelegramActionConfirmation,
    profile: UserProfileCRUD,
) : DeleteAccount {
    private val sender = TelegramConfirmationSender(startConfirmation, profile)

    override suspend fun invoke(): ActionOutcome<Unit> {
        return sender.send(SensitiveAction.DELETE_ACCOUNT)
    }
}
