package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
enum class BookingPeriodRemote {
    UPCOMING,
    PAST,
}
