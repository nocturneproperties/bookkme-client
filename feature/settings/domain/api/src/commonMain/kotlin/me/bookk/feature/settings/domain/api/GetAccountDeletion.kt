package me.bookk.feature.settings.domain.api

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface GetAccountDeletion {
    fun flow(): Flow<Instant?>
    suspend fun refresh()
}
