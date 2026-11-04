package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus

interface GetLegalDocumentStatuses {
    suspend operator fun invoke(): List<LegalDocumentStatus>
}
