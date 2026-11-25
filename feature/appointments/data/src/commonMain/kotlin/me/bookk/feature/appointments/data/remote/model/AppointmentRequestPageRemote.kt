package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest

@Serializable
data class AppointmentRequestPageRemote(
    @ProtoNumber(1) val data: List<AppointmentRequestRemote>,
    @ProtoNumber(2) val nextCursor: String? = null,
) {
    fun toDomain(): Page<AppointmentRequest> {
        return Page(items = data.map { it.toDomain() }, nextCursor = nextCursor)
    }
}
