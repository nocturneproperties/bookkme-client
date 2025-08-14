package me.bookk.feature.business.data.mapping

import library.money.api.CurrencyFactory
import me.bookk.database.entity.BusinessEntity
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo

internal fun BusinessRemote.toDomain(): Business {
    return Business(
        id = id,
        name = name,
        description = description,
        address = address,
        location = location?.let {
            Business.Location(
                lat = it.lat,
                lng = it.lng
            )
        },
        currency = CurrencyFactory.forCode(currencyCode),
        socials = socials.map(BusinessRemote.Social::toDomain).toSet()
    )
}

internal fun BusinessRemote.Social.toDomain():Business.Social {
    return Business.Social(
        kind = kind.domainKind,
        value = value
    )

}

internal fun Business.toLocal(): BusinessEntity {
    return BusinessEntity(
        id = id,
        name = name,
        description = description,
        address = address,
        locationLat = location?.lat,
        locationLng = location?.lng,
        currencyCode = currency.code(),
        phone = socials.firstOrNull { it.kind == Business.SocialKind.PHONE }?.value,
        insta = socials.firstOrNull { it.kind == Business.SocialKind.INSTAGRAM }?.value,
        viber = socials.firstOrNull { it.kind == Business.SocialKind.VIBER }?.value,
        whatsApp = socials.firstOrNull { it.kind == Business.SocialKind.WHATSAPP }?.value,
        telegram = socials.firstOrNull { it.kind == Business.SocialKind.TELEGRAM }?.value
    )
}

internal fun BusinessEntity.toDomain(): Business {
    return Business(
        id = id,
        name = name,
        description = description,
        address = address,
        location = if (locationLat != null && locationLng != null) {
            Business.Location(locationLat!!, locationLng!!)
        } else null,
        currency = CurrencyFactory.forCode(currencyCode),
        socials = setOf(
            Business.Social(Business.SocialKind.PHONE, phone),
            Business.Social(Business.SocialKind.INSTAGRAM, insta),
            Business.Social(Business.SocialKind.VIBER, viber),
            Business.Social(Business.SocialKind.WHATSAPP, whatsApp),
            Business.Social(Business.SocialKind.TELEGRAM, telegram)
        )
    )
}

internal fun UserBusinessesRemote.toUserBusinesses():UserBusinessInfo {
    return UserBusinessInfo(
        dashboardId = dashboardId,
        businesses = businesses.map(BusinessRemote::toDomain)
    )
}