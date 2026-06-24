package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.BusinessSnapshot
import kotlin.uuid.Uuid

@Serializable
data class BusinessSnapshotRemote(
    val id: Uuid,
    val name: String,
    val address: String,
    val timeZone: TimeZone,
    val isEnabled: Boolean
) {
    fun toDomain() = BusinessSnapshot(
        id = id,
        name = name,
        address = address,
        isEnabled = isEnabled
    )
}