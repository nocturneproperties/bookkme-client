package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.entity.Appointment

@Serializable
data class AppointmentPageRemote(
    @ProtoNumber(1) val data: List<AppointmentRemote>,
    @ProtoNumber(3) val nextCursor: String? = null,
) {
    fun toDomain(): Page<Appointment> {
        return Page(items = data.map { it.toDomain() }, nextCursor = nextCursor)
    }
}
