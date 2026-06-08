package me.bookk.di.feature

import me.bookk.feature.appointments.data.di.appointmentsDataModule
import me.bookk.feature.appointments.domain.impl.di.appointmentsDomainModule
import me.bookk.feature.appointments.presentation.di.appointmentsPresentationModule
import org.koin.dsl.module

internal fun appointmentsDiModule() = module {
    includes(
        appointmentsPresentationModule(),
        appointmentsDataModule(),
        appointmentsDomainModule()
    )
}