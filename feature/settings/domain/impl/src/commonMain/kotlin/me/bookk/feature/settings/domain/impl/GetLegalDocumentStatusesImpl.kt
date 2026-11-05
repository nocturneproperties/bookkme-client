package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource

internal class GetLegalDocumentStatusesImpl(
    private val dataSource: LegalDocumentsDataSource
) : GetLegalDocumentStatuses {
    override fun flow(): Flow<List<LegalDocumentStatus>> {
        return dataSource.observeStatuses()
    }

    override suspend fun refresh(): List<LegalDocumentStatus> {
        return dataSource.getStatuses().also { dataSource.saveStatuses(it) }
    }
}
