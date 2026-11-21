package me.bookk.feature.settings.data.mapping

import me.bookk.database.entity.LegalDocumentStatusEntity
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion

internal fun LegalDocumentStatus.toEntity(): LegalDocumentStatusEntity {
    return LegalDocumentStatusEntity(
        document = current.document.name,
        version = current.version,
        url = current.url,
        effectiveAt = current.effectiveAt,
        acceptedAt = acceptedAt,
    )
}

internal fun LegalDocumentStatusEntity.toDomain(): LegalDocumentStatus {
    return LegalDocumentStatus(
        current = LegalDocumentVersion(
            document = LegalDocument.valueOf(document),
            version = version,
            url = url,
            effectiveAt = effectiveAt,
        ),
        acceptedAt = acceptedAt,
    )
}
