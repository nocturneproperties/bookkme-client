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
        socials = socials.map(BusinessRemote.Social::toDomain).associateBy { it.kind }
    )
}

internal fun BusinessRemote.Social.toDomain(): Business.Social {
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
        phone = socials[Business.SocialKind.PHONE]?.value,
        insta = socials[Business.SocialKind.INSTAGRAM]?.value,
        viber = socials[Business.SocialKind.VIBER]?.value,
        whatsApp = socials[Business.SocialKind.WHATSAPP]?.value,
        telegram = socials[Business.SocialKind.TELEGRAM]?.value
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
        socials = listOf(
            Business.Social(Business.SocialKind.PHONE, phone),
            Business.Social(Business.SocialKind.INSTAGRAM, insta),
            Business.Social(Business.SocialKind.VIBER, viber),
            Business.Social(Business.SocialKind.WHATSAPP, whatsApp),
            Business.Social(Business.SocialKind.TELEGRAM, telegram)
        ).associateBy { it.kind }
    )
}

internal fun UserBusinessesRemote.toUserBusinesses(): UserBusinessInfo {
    return UserBusinessInfo(
        dashboardId = dashboardId,
        businesses = businesses.map(BusinessRemote::toDomain)
    )
}

internal fun Business.toRemote(): BusinessRemote {
    return BusinessRemote(
        id = id,
        name = name,
        description = description,
        address = address,
        location = location?.let {
            BusinessRemote.Location(
                lat = it.lat,
                lng = it.lng
            )
        },
        currencyCode = currency.code(),
        socials = socials.values.map { it.toRemote() }
    )
}

internal fun Business.Social.toRemote(): BusinessRemote.Social {
    return BusinessRemote.Social(
        kind = BusinessRemote.SocialKind.entries.first { it.domainKind == kind },
        value = value
    )
}