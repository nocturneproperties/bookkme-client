package me.bookk.feature.booking.domain.api

import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import kotlin.uuid.Uuid

interface SetBookingServices {
    suspend operator fun invoke(
        businessId: Uuid,
        services: List<BookingPreparation.PreparedService>,
    )
}
