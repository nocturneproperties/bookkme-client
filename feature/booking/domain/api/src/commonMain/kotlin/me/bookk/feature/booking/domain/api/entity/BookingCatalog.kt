package me.bookk.feature.booking.domain.api.entity

data class BookingCatalog(
    val businessName: String?,
    val groups: List<BookingServiceGroup>,
)
