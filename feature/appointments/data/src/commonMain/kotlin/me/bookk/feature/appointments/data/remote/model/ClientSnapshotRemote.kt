package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import kotlin.uuid.Uuid

@Serializable
data class ClientSnapshotRemote(
    val id: Uuid,
    val fullName: String,
    val phone: String,
    val email: String
) {
    fun toDomain() = ClientSnapshot(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email
    )
}