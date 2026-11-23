package me.bookk.feature.business.data.mapping

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.database.entity.BusinessDataExportEntity
import kotlin.uuid.Uuid

internal fun DataExport.toEntity(businessId: Uuid): BusinessDataExportEntity {
    return BusinessDataExportEntity(
        businessId = businessId,
        status = status.name,
        requestedAt = requestedAt,
        expiresAt = expiresAt,
        availableAgainAt = availableAgainAt,
    )
}

internal fun BusinessDataExportEntity.toDomain(): DataExport {
    return DataExport(
        status = DataExportStatus.valueOf(status),
        requestedAt = requestedAt,
        expiresAt = expiresAt,
        availableAgainAt = availableAgainAt,
    )
}
