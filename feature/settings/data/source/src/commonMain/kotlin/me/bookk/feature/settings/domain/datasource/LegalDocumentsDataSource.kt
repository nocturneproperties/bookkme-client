package me.bookk.feature.settings.domain.datasource

import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion

interface LegalDocumentsDataSource {
    suspend fun accept(documents: AcceptedLegalDocuments)
    suspend fun getVersions(): List<LegalDocumentVersion>
    suspend fun getStatuses(): List<LegalDocumentStatus>
}
