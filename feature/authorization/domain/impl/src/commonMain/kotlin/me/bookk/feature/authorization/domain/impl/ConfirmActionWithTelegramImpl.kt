package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource

internal class ConfirmActionWithTelegramImpl(
    private val dataSource: AuthorizationDataSource,
) : ConfirmActionWithTelegram {
    override suspend fun invoke(initData: String): String {
        return dataSource.confirmActionWithTelegram(initData)
    }
}
