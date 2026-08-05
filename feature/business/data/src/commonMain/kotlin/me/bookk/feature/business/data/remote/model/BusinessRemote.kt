package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

@Serializable
class BusinessRemote(
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val location: Location?,
    val currencyCode: String,
    val socials: List<Social>,
    val schedule: ScheduleRemote
) {
    @Serializable
    class Location(
        val lat: Double,
        val lng: Double
    )

    @Serializable
    class Social(
        val kind: SocialKind,
        val value: String?
    )

    enum class SocialKind(val domainKind: Business.SocialKind) {
        PHONE(Business.SocialKind.PHONE),
        INSTAGRAM(Business.SocialKind.INSTAGRAM),
        TELEGRAM(Business.SocialKind.TELEGRAM),
        VIBER(Business.SocialKind.VIBER),
        WHATSAPP(Business.SocialKind.WHATSAPP)
    }
}