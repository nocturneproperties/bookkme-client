package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.api.entity.ActionOutcome

internal class TelegramStartPersonalDataExportImpl(
    startConfirmation: StartTelegramActionConfirmation,
    profile: UserProfileCRUD,
) : StartPersonalDataExport {
    private val sender = TelegramConfirmationSender(startConfirmation, profile)

    override suspend fun invoke(): ActionOutcome<DataExport> {
        return sender.send(SensitiveAction.EXPORT_PERSONAL_DATA)
    }
}
