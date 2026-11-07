package me.bookk.feature.settings.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus

interface GetLegalDocumentStatuses {
    fun flow(): Flow<List<LegalDocumentStatus>>
    suspend fun refresh(): List<LegalDocumentStatus>
}
