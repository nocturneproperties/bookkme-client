package me.bookk.feature.services.data.mapper

import library.money.api.Money
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.relation.ServiceLocal
import me.bookk.feature.services.domain.api.service.entity.Service

internal fun Service.toDb(): ServiceEntity {
    return ServiceEntity(
        id = id,
        businessId = businessId,
        groupId = group.id,
        name = name,
        duration = duration,
        priceCurrency = price.currency.code(),
        priceValue = price.value,
        isAvailable = isAvailable
    )
}

internal fun ServiceLocal.toDomain(): Service {
    return Service(
        id = entity.id,
        businessId = entity.businessId,
        group = group.toDomain(),
        name = entity.name,
        duration = entity.duration,
        price = Money(
            value = entity.priceValue,
            currencyType = Money.SupportedCurrency.fromCode(entity.priceCurrency)
        ),
        isAvailable = entity.isAvailable
    )
}