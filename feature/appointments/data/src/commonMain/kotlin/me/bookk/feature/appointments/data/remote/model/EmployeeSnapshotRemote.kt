package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import kotlin.uuid.Uuid

@Serializable
data class EmployeeSnapshotRemote(
    val id: Uuid,
    val userId: Uuid,
    val fullName: String
) {
    fun toDomain() = EmployeeSnapshot(id = id, userId = userId, fullName = fullName)
}
