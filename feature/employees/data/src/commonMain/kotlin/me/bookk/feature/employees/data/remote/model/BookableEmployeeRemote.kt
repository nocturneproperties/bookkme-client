package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.uuid.Uuid

@Serializable
internal class BookableEmployeeRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val name: String,
    @ProtoNumber(4) val lastName: String,
    @ProtoNumber(5) val services: List<ServiceRemote>,
    @ProtoNumber(6) val schedule: ScheduleRemote,
) {
    fun toDomain(): BookableEmployee {
        return BookableEmployee(
            id = id,
            businessId = businessId,
            name = name,
            lastName = lastName,
            services = services.map { it.toDomain() },
            schedule = schedule.toDomain(),
        )
    }
}
