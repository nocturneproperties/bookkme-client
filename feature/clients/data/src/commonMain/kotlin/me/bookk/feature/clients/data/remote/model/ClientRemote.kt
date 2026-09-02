package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
internal class ClientRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val lastName: String,
    @ProtoNumber(4) val phone: String,
    @ProtoNumber(5) val email: String,
    @ProtoNumber(6) val userId: Uuid?,
    @ProtoNumber(7) val description: String? = null
)

@Serializable
internal class ClientUpdateRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val lastName: String,
    @ProtoNumber(4) val phone: String,
    @ProtoNumber(5) val email: String,
    @ProtoNumber(6) val description: String?
)
