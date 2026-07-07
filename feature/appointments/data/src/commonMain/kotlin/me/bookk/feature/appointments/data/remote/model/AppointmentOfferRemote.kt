package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentOfferRemote(
    val request: AppointmentRequestRemote,
    val offerToken: String
)
