package me.bookk.feature.business.domain.api.entity

import kotlinx.datetime.TimeZone
import library.money.api.Currency
import kotlin.uuid.Uuid

data class Business(
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    val location: Location?,
    val currency: Currency,
    val timeZone: TimeZone,
    val socials: Map<SocialKind, Social>,
    val schedule: WorkingSchedule,
    val permissions: BusinessPermissions
) {
    data class Update(
        val id: Uuid,
        val name: String,
        val description: String,
        val address: String,
        val location: Location?,
        val currency: Currency,
        val socials: Map<SocialKind, Social>,
        val schedule: WorkingSchedule
    )

    data class Location(
        val lat: Double,
        val lng: Double
    ) {
        override fun toString(): String {
            return "$lat, $lng"
        }
    }

    data class Social(
        val kind: SocialKind,
        val value: String?
    )

    enum class SocialKind {
        PHONE,
        INSTAGRAM,
        TELEGRAM,
        VIBER,
        WHATSAPP
    }
}