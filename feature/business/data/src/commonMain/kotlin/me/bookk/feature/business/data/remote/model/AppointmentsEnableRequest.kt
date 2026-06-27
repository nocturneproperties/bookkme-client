package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer
import kotlin.uuid.Uuid

@Serializable
internal data class AppointmentsEnableRequest(
    val id: Uuid,
    val name: String,
    val address: String,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val isEnabled: Boolean
)