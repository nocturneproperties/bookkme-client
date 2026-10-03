package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
data class AppointmentCancellationRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(3) val reason: String
)
