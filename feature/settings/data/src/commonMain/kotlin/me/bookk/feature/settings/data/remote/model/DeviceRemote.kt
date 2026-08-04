package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.settings.domain.api.entity.Device
import me.bookk.feature.settings.domain.api.entity.DeviceLanguage
import kotlin.uuid.Uuid

@Serializable
data class DeviceRemote(
    val id: Uuid,
    val authId: Uuid,
    val deviceUuid: Uuid,
    val userId: Uuid,
    val notificationToken: String? = null,
    val language: DeviceLanguageRemote
) {
    fun toDomain() = Device(
        id = id,
        authId = authId,
        deviceUuid = deviceUuid,
        userId = userId,
        notificationToken = notificationToken,
        language = language.toDomain()
    )
}

@Serializable
enum class DeviceLanguageRemote {
    EN,
    UK;

    fun toDomain() = when (this) {
        EN -> DeviceLanguage.EN
        UK -> DeviceLanguage.UK
    }
}

@Serializable
data class UpdateTokenRequestRemote(val token: String)
