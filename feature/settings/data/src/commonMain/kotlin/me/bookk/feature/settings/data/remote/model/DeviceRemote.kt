package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.settings.domain.api.entity.Device
import kotlin.uuid.Uuid

@Serializable
data class DeviceRemote(
    val id: Uuid,
    val authId: Uuid,
    val deviceUuid: Uuid,
    val userId: Uuid,
    val notificationToken: String? = null
) {
    fun toDomain() = Device(
        id = id,
        authId = authId,
        deviceUuid = deviceUuid,
        userId = userId,
        notificationToken = notificationToken
    )
}

@Serializable
data class UpdateTokenRequestRemote(val token: String)
