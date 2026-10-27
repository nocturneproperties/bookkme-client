package me.bookk.feature.settings.domain.api.entity

import kotlin.uuid.Uuid

data class Device(
    val id: Uuid,
    val authId: Uuid,
    val deviceUuid: Uuid,
    val userId: Uuid,
    val notificationToken: String?,
    val language: DeviceLanguage
) {
    companion object {
        fun stub(
            deviceUuid: Uuid = Uuid.random(),
            notificationToken: String? = "token",
            language: DeviceLanguage = DeviceLanguage.EN
        ) = Device(
            id = Uuid.random(),
            authId = Uuid.random(),
            deviceUuid = deviceUuid,
            userId = Uuid.random(),
            notificationToken = notificationToken,
            language = language
        )
    }
}

enum class DeviceLanguage {
    EN,
    UK
}
