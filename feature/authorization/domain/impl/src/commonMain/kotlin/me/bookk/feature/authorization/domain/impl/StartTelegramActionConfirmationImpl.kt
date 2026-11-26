package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramActionConfirmationStart
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

internal class StartTelegramActionConfirmationImpl(
    private val dataSource: AuthorizationDataSource,
) : StartTelegramActionConfirmation {
    override suspend fun invoke(action: SensitiveAction, businessId: Uuid, targetId: Uuid) {
        dataSource.startTelegramActionConfirmation(
            TelegramActionConfirmationStart(action, businessId, targetId),
        )
    }
}
