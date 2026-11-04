package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes

internal class AcceptLegalDocumentsImpl(private val dataSource: LegalDocumentsDataSource) :
    AcceptLegalDocuments {
    override suspend fun invoke(documents: AcceptedLegalDocuments) {
        runCatching { dataSource.accept(documents) }
            .onBusinessError {
                when (it.errorCode) {
                    SettingsErrorCodes.LEGAL_DOCUMENT_VERSION_UNKNOWN ->
                        throw AcceptLegalDocuments.Error.UnknownVersion()
                }
            }
            .getOrThrow()
    }
}
