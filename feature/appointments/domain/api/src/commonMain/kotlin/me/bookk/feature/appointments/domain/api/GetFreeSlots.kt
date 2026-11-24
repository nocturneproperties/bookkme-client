package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery

interface GetFreeSlots {
    suspend operator fun invoke(query: FreeSlotsQuery): List<FreeSlotsDay>

    sealed interface Error {
        class AppointmentsNotEnabled :
            Throwable(),
            Error
        class InvalidServiceSelection :
            Throwable(),
            Error
        class InvalidRange :
            Throwable(),
            Error
    }
}
