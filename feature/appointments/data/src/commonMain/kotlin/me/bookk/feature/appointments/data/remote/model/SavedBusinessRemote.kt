package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class SavedBusinessRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val enabled: Boolean = false,
    @ProtoNumber(3) val savedAt: Instant,
    @ProtoNumber(4) val lastVisitAt: Instant? = null,
) {
    fun toDomain(): SavedBusinessEntry {
        return SavedBusinessEntry(
            businessId = businessId,
            isEnabled = enabled,
            savedAt = savedAt,
            lastVisitAt = lastVisitAt,
        )
    }
}
