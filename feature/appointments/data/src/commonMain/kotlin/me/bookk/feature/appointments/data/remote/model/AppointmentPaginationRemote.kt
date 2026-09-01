package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class AppointmentPaginationRemote(
    @ProtoNumber(1) val data: List<AppointmentRemote>,
    @ProtoNumber(2) val metadata: PaginationMetadataRemote
)

@Serializable
data class PaginationMetadataRemote(
    @ProtoNumber(1) val total: Int,
    @ProtoNumber(2) val page: Int,
    @ProtoNumber(3) val pageSize: Int
)
