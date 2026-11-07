package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion

interface GetLegalDocumentVersions {
    suspend operator fun invoke(): List<LegalDocumentVersion>
}
