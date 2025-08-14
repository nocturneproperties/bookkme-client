package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.business.domain.api.entity.Business

@Serializable
class BusinessRemote(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location?,
    val currencyCode: String,
    val socials: List<Social>
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