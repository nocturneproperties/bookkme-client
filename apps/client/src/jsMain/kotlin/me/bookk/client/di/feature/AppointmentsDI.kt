package me.bookk.client.di.feature

import me.bookk.feature.appointments.data.di.clientAppointmentsDataModule
import me.bookk.feature.appointments.domain.impl.di.clientAppointmentsDomainModule
import org.koin.dsl.module

internal fun appointmentsDiModule() = module {
    includes(
        clientAppointmentsDomainModule(),
        clientAppointmentsDataModule(),
    )
}
