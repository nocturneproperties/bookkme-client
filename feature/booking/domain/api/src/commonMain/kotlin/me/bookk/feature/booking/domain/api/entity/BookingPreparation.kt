package me.bookk.feature.booking.domain.api.entity

data class BookingPreparation(
    val services: List<PreparedService>,
) {
    data class PreparedService(
        val service: BookingService,
        val quantity: Int,
    )
}
