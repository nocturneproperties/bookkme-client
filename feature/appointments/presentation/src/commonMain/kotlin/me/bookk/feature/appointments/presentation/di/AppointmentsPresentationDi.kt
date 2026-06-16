package me.bookk.feature.appointments.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformAppointmentsDiModule(): Module

fun appointmentsPresentationModule() = module {
    includes(platformAppointmentsDiModule())
}