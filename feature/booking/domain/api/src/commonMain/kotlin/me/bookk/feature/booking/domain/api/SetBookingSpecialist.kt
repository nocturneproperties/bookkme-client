package me.bookk.feature.booking.domain.api

import kotlin.uuid.Uuid

interface SetBookingSpecialist {
    suspend operator fun invoke(businessId: Uuid, specialistId: Uuid)
}
