package me.bookk.feature.business.domain.api.entity

import library.money.api.Currency
import kotlin.uuid.Uuid

class Business(
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    val location: Location?,
    val currency: Currency,
    val socials: Set<Social>
) {
    data class Location(
        val lat: Double,
        val lng: Double
    )

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