package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import kotlin.uuid.Uuid

@Serializable
data class ClientSnapshotRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val fullName: String,
    @ProtoNumber(3) val phone: String?,
    @ProtoNumber(4) val email: String?,
    @ProtoNumber(5) val userId: Uuid? = null
) {
    fun toDomain(): ClientSnapshot {
        return ClientSnapshot(
            id = id,
            fullName = fullName,
            phone = phone,
            email = email,
            userId = userId
        )
    }
}
