package me.bookk.feature.settings.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion

interface LegalDocumentsDataSource {
    suspend fun accept(documents: AcceptedLegalDocuments)
    suspend fun getVersions(): List<LegalDocumentVersion>
    suspend fun getStatuses(): List<LegalDocumentStatus>
    fun observeStatuses(): Flow<List<LegalDocumentStatus>>
    suspend fun saveStatuses(statuses: List<LegalDocumentStatus>)
}
