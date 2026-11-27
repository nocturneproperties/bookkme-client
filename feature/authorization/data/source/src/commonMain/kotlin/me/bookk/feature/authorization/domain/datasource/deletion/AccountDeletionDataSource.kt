package me.bookk.feature.authorization.domain.datasource.deletion

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface AccountDeletionDataSource {
    suspend fun getScheduledDeletion(): Instant?
    suspend fun cancelScheduledDeletion()
    fun observeScheduledDeletion(): Flow<Instant?>
    suspend fun saveScheduledDeletion(deletesAt: Instant?)
}
