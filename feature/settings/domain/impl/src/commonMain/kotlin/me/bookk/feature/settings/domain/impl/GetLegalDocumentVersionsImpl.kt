package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.GetLegalDocumentVersions
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource

internal class GetLegalDocumentVersionsImpl(
    private val dataSource: LegalDocumentsDataSource
) : GetLegalDocumentVersions {
    override suspend fun invoke(): List<LegalDocumentVersion> {
        return dataSource.getVersions()
    }
}
