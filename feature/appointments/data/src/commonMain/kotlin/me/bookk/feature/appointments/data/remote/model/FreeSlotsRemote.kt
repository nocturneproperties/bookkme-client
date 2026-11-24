package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.FreeSlot
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class FreeSlotsRequestRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val employeeId: Uuid,
    @ProtoNumber(3) val services: List<RequestedServiceRemote>,
    @ProtoNumber(4) val from: LocalDate,
    @ProtoNumber(5) val to: LocalDate,
) {
    companion object {
        fun fromDomain(query: FreeSlotsQuery): FreeSlotsRequestRemote {
            return FreeSlotsRequestRemote(
                businessId = query.businessId,
                employeeId = query.employeeId,
                services = query.services.map { RequestedServiceRemote(it.serviceId, it.count) },
                from = query.from,
                to = query.to,
            )
        }
    }
}

@Serializable
data class FreeSlotsDayRemote(
    @ProtoNumber(1) val date: LocalDate,
    @ProtoNumber(2) val slots: List<FreeSlotRemote> = emptyList(),
) {
    fun toDomain(): FreeSlotsDay {
        return FreeSlotsDay(date = date, slots = slots.map { it.toDomain() })
    }
}

@Serializable
data class FreeSlotRemote(
    @ProtoNumber(1) val start: Instant,
    @ProtoNumber(2) val end: Instant,
) {
    fun toDomain(): FreeSlot {
        return FreeSlot(start = start, end = end)
    }
}
