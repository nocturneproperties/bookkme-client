package me.bookk.feature.booking.presentation.screen

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.resources.toStringDesc
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import kotlin.time.Duration

internal fun List<BookingService>.bookingTotal(): BookingTotal? {
    if (isEmpty()) return null
    return BookingTotal(
        summary = BookingRes.strings.booking_services_summary.format(
            BookingRes.plurals.booking_services_count.format(size, size),
            totalDuration().toStringDesc(),
        ),
        price = totalPrice().toString().desc(),
    )
}

internal fun List<PreparedService>.units(): List<BookingService> {
    return flatMap { prepared -> List(prepared.quantity) { prepared.service } }
}

internal fun List<BookingService>.totalDuration(): Duration {
    return fold(Duration.ZERO) { total, service -> total + service.duration }
}

internal fun List<BookingService>.totalPrice(): Money {
    return map { it.price }.reduce { total, price -> total + price }
}
