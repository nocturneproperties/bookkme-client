package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal data class AppointmentsEnableRequest(
    val id: Uuid,
    val name: String,
    val address: String,
    val timeZone: TimeZone,
    val isEnabled: Boolean
)