package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.settings.domain.api.entity.Device
import me.bookk.feature.settings.domain.api.entity.DeviceLanguage
import kotlin.uuid.Uuid

@Serializable
data class DeviceRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val authId: Uuid,
    @ProtoNumber(3) val deviceUuid: Uuid,
    @ProtoNumber(4) val userId: Uuid,
    @ProtoNumber(5) val notificationToken: String? = null,
    @ProtoNumber(6) val language: DeviceLanguageRemote
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
data class UpdateTokenRequestRemote(@ProtoNumber(1) val token: String)
