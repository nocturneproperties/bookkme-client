package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions

interface ObserveAppointmentOptions {
    operator fun invoke(): Flow<AppointmentOptions>
}
