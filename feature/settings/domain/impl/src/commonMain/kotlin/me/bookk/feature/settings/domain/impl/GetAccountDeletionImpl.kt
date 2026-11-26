package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import me.bookk.feature.settings.domain.api.GetAccountDeletion
import kotlin.time.Instant

internal class GetAccountDeletionImpl(
    private val dataSource: AccountDeletionDataSource,
) : GetAccountDeletion {
    override fun flow(): Flow<Instant?> {
        return dataSource.observeScheduledDeletion()
    }

    override suspend fun refresh() {
        dataSource.saveScheduledDeletion(dataSource.getScheduledDeletion())
    }
}
