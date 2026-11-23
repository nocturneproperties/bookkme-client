package me.bookk.feature.settings.data.mapping

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.database.entity.PersonalDataExportEntity

internal fun DataExport.toEntity(): PersonalDataExportEntity {
    return PersonalDataExportEntity(
        status = status.name,
        requestedAt = requestedAt,
        expiresAt = expiresAt,
        availableAgainAt = availableAgainAt,
    )
}

internal fun PersonalDataExportEntity.toDomain(): DataExport {
    return DataExport(
        status = DataExportStatus.valueOf(status),
        requestedAt = requestedAt,
        expiresAt = expiresAt,
        availableAgainAt = availableAgainAt,
    )
}
