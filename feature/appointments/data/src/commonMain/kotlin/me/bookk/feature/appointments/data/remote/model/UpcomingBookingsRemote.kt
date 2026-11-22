package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings

@Serializable
data class UpcomingBookingsRemote(
    @ProtoNumber(1) val appointments: List<AppointmentRemote> = emptyList(),
    @ProtoNumber(2) val requests: List<AppointmentRequestRemote> = emptyList(),
) {
    fun toDomain(): UpcomingBookings {
        return UpcomingBookings(
            appointments = appointments.map { it.toDomain() },
            requests = requests.map { it.toDomain() },
        )
    }
}
