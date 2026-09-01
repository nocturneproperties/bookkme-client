package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import kotlin.uuid.Uuid

@Serializable
data class EmployeeSnapshotRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val userId: Uuid,
    @ProtoNumber(3) val fullName: String
) {
    fun toDomain() = EmployeeSnapshot(id = id, userId = userId, fullName = fullName)
}
