package me.bookk.feature.appointments.data.mapping

import me.bookk.database.entity.SavedBusinessEntity
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry

internal fun SavedBusinessEntry.toEntity(): SavedBusinessEntity {
    return SavedBusinessEntity(
        businessId = businessId,
        enabled = isEnabled,
        savedAt = savedAt,
        lastVisitAt = lastVisitAt,
    )
}

internal fun SavedBusinessEntity.toDomain(): SavedBusinessEntry {
    return SavedBusinessEntry(
        businessId = businessId,
        isEnabled = enabled,
        savedAt = savedAt,
        lastVisitAt = lastVisitAt,
    )
}
