package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled
import kotlin.uuid.Uuid

@Serializable
internal class BusinessAppointmentsEnabledRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val enabled: Boolean,
) {
    fun toDomain(): BusinessAppointmentsEnabled {
        return BusinessAppointmentsEnabled(businessId, enabled)
    }
}
