package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource

internal class GetLegalDocumentStatusesImpl(
    private val dataSource: LegalDocumentsDataSource
) : GetLegalDocumentStatuses {
    override suspend fun invoke(): List<LegalDocumentStatus> {
        return dataSource.getStatuses()
    }
}
