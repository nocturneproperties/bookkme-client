package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import kotlin.uuid.Uuid

interface GetAppointmentOptions {
    suspend operator fun invoke(businessId: Uuid): AppointmentOptions
}