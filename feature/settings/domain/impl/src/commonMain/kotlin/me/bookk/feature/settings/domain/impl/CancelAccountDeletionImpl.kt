package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import me.bookk.feature.settings.domain.api.CancelAccountDeletion

internal class CancelAccountDeletionImpl(
    private val dataSource: AccountDeletionDataSource,
) : CancelAccountDeletion {
    override suspend fun invoke() {
        dataSource.cancelScheduledDeletion()
        dataSource.saveScheduledDeletion(null)
    }
}
