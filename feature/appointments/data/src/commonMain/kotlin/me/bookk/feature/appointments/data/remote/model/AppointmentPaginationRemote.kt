package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentPaginationRemote(
    val data: List<AppointmentRemote>,
    val metadata: PaginationMetadataRemote
)

@Serializable
data class PaginationMetadataRemote(
    val total: Int,
    val page: Int,
    val pageSize: Int
)
