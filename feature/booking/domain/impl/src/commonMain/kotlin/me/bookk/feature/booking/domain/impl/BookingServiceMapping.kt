package me.bookk.feature.booking.domain.impl

import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.services.domain.api.service.entity.Service

internal fun List<Service>.bookable(): List<Service> {
    return filter { it.isAvailable }
}

internal fun Service.toBookingService(): BookingService {
    return BookingService(id = id, name = name, duration = duration, price = price)
}
